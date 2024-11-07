# SecurityContextHolder 的使用

SecurityContextHolder 是 Spring Security 的核心组件，用于存储应用程序的安全上下文（SecurityContext）。它使用 ThreadLocal 来存储认证信息，这意味着同一个线程中的任何方法都可以获取到存储的认证信息。

## 一、基本概念

```java
public class SecurityContextHolder {
    // 默认使用 ThreadLocal 模式
    private static SecurityContextHolderStrategy strategy;

    // 存储策略
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";  // 默认
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";  // 支持子线程
    public static final String MODE_GLOBAL = "MODE_GLOBAL";  // 全局模式
}
```

SecurityContextHolder 包含三个重要概念：

1. SecurityContext：安全上下文
2. Authentication：认证信息
3. Principal：当前认证的主体

## 二、向 SecurityContextHolder 注入认证信息

### 1. 通过 Authentication 对象注入

```java:src/main/java/com/example/security/SecurityService.java
@Service
@Slf4j
public class SecurityService {

    public void authenticateUser(String username, Collection<? extends GrantedAuthority> authorities) {
        // 1. 创建认证对象
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            username,    // principal
            null,       // credentials (已认证后通常为null)
            authorities // 权限列表
        );

        // 2. 设置认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("User {} has been authenticated with authorities: {}",
                username, authorities);
    }
}
```

### 2. 通过 AuthenticationManager 注入

```java:src/main/java/com/example/service/AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public void login(String username, String password) {
        try {
            // 1. 创建未认证的 Authentication
            Authentication unAuthToken = new UsernamePasswordAuthenticationToken(
                username,
                password
            );

            // 2. 通过 AuthenticationManager 认证
            Authentication authentication = authenticationManager.authenticate(unAuthToken);

            // 3. 认证成功，存储认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
    }
}
```

### 3. 在过滤器中注入

```java:src/main/java/com/example/security/JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 创建认证信息并注入
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }
}
```

## 三、从 SecurityContextHolder 获取信息

### 1. 工具类方式

```java:src/main/java/com/example/security/SecurityUtils.java
@Component
public class SecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UserPrincipal getCurrentUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        return (UserPrincipal) auth.getPrincipal();
    }

    public static boolean hasRole(String role) {
        Authentication auth = getAuthentication();
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
```

### 2. 注解方式（推荐）

```java:src/main/java/com/example/controller/UserController.java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserPrincipal user) {
        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        // 方法上的注解会自动检查权限
        return userService.findAll();
    }
}
```

## 四、异步操作中的处理

### 1. 配置异步支持

```java:src/main/java/com/example/config/AsyncConfig.java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @PostConstruct
    public void enableAuthenticationContextOnSpawnedThreads() {
        // 设置 SecurityContextHolder 策略为支持子线程
        SecurityContextHolder.setStrategyName(
            SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);

        // 包装执行器以支持 SecurityContext 传递
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}
```

### 2. 在异步方法中使用

```java:src/main/java/com/example/service/AsyncService.java
@Service
public class AsyncService {

    @Async
    public CompletableFuture<String> processDataAsync() {
        // 在异步方法中仍然可以访问认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 处理数据...
        return CompletableFuture.completedFuture("Processed by: " + username);
    }
}
```

## 五、最佳实践

1. **安全处理**

```java
try {
    // 设置认证信息
    SecurityContextHolder.getContext().setAuthentication(authentication);
    // 执行业务逻辑
} finally {
    // 清理认证信息
    SecurityContextHolder.clearContext();
}
```

2. **异常处理**

```java
public void secureOperation() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new UnauthorizedException("Authentication required");
    }

    try {
        // 执行需要认证的操作
    } catch (Exception e) {
        // 记录安全相关异常
        log.error("Security error for user: {}", auth.getName(), e);
        throw e;
    }
}
```

3. **避免重复获取**

```java
public class SecurityAwareService {
    private Authentication cachedAuth;

    public void process() {
        if (cachedAuth == null) {
            cachedAuth = SecurityContextHolder.getContext().getAuthentication();
        }
        // 使用缓存的认证信息
    }
}
```

通过以上方式，我们可以有效地管理和使用 SecurityContextHolder，确保应用程序的安全性和可维护性。

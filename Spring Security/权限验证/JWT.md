# JWT

> 本文关联的代码仓库参阅：[Spring-Series/examples](https://github.com/wx-chevalier/Spring-Series)

本文将会详细介绍 Spring Boot 中集成 Spring Security 并基于 JWT 进行用户权限验证的案例，下图（图片源自 [bezkoder.com/spring-boot-jwt-authentication](https://bezkoder.com/spring-boot-jwt-authentication/)）显示了我们如何实现用户注册、用户登录和授权的流程：

![请求与响应交互流程](https://s3.ax1x.com/2021/02/25/yvC7iq.png)

如果客户端访问受保护的资源，必须在 HTTP 授权头中添加一个合法的 JWT。你可以通过下图来了解我们的 Spring Boot Server。

![Spring Boot Server](https://s3.ax1x.com/2021/02/25/yvPym4.png)

- WebSecurityConfigurerAdapter 是我们安全实现的核心。它提供了 HttpSecurity 配置来配置 cors、csrf、会话管理、受保护资源的规则。我们也可以扩展和定制默认的配置，它包含下面的元素。
- UserDetailsService 接口有一个通过用户名加载 User 的方法，并返回一个 UserDetails 对象，Spring Security 可以用来进行认证和验证。
- UserDetails 包含必要的信息（如：用户名、密码、权限）来构建一个认证对象。
- UsernamePasswordAuthenticationToken 从登录请求中获取{用户名，密码}，AuthenticationManager 将使用它来验证登录账户。
- AuthenticationManager 有一个 DaoAuthenticationProvider（在 UserDetailsService 和 PasswordEncoder 的帮助下）来验证 UsernamePasswordAuthenticationToken 对象。如果成功，AuthenticationManager 返回一个完全填充的 Authentication 对象（包括授权）。
- OncePerRequestFilter 对我们的 API 的每个请求进行一次执行。它提供了一个 doFilterInternal()方法，我们将实现解析和验证 JWT，加载 User 详情（使用 UserDetailsService），检查 Authorizaion（使用 UsernamePasswordAuthenticationToken）。
- AuthenticationEntryPoint 将捕获认证错误。

# 安全配置

在 Spring Boot 应用中，首先我们需要声明继承了 WebSecurityConfigurerAdapter 类的配置：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Resource(name = "userService")
  private UserDetailsService userDetailsService;

  @Autowired private JwtAuthenticationEntryPoint unauthorizedHandler;

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Autowired
  public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
  }

  @Bean
  public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
    return new JwtAuthenticationFilter();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/auth/*", "/noauth/*")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(
        authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public BCryptPasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }
}
```

- 在该配置中我们声明了 BCryptPasswordEncoder 作为全局的加密编码器，然后将它指定为 userDetailsService 的默认编码器；该编码器会在用户注册与鉴权时使用。
- 在 configure 方法中可以配置需要过滤的路由，并且指定自定义的 JwtAuthenticationFilter 作为过滤器。

```java
public class Constants {
  public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
  public static final String SIGNING_KEY = "wx123321";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
  public static final String AUTHORITIES_KEY = "scopes";
}

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private UserDetailsService userDetailsService;

  @Autowired private TokenProvider jwtTokenUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    String header = req.getHeader(HEADER_STRING);
    String username = null;
    String authToken = null;
    if (header != null && header.startsWith(TOKEN_PREFIX)) {
      authToken = header.replace(TOKEN_PREFIX, "");
      // ...
    } else {
      logger.warn("couldn't find bearer string, will ignore the header");
    }
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtTokenUtil.validateToken(authToken, userDetails)) {
        UsernamePasswordAuthenticationToken authentication =
            jwtTokenUtil.getAuthentication(
                authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        logger.info("authenticated user " + username + ", setting security context");
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    chain.doFilter(req, res);
  }
}

```

在该类的 doFilterInternal 方法中，我们会依次对 Authorization 的值进行判断，并且通过注入的 UserDetailsService 来查找用户，这里的用户名或者其他信息就是来自于 JWT 的 Token。其中对于 Token 的解析，我们是在单独类进行处理：

```java
@Component
public class TokenProvider implements Serializable {

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(Authentication authentication) {
    // ...
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * 解析 Token，获取详细信息
   *
   * @param token
   * @param existingAuth
   * @param userDetails
   * @return
   */
  UsernamePasswordAuthenticationToken getAuthentication(
      final String token, final Authentication existingAuth, final UserDetails userDetails) {

    final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);

    final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

    final Claims claims = claimsJws.getBody();

    final Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
  }
}
```

我们也可以自定义鉴权失败的处理类：

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
      // This is invoked when user tries to access a secured REST resource without supplying any credentials
      // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
      // Here you can place any message you want
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
   }
}
```

在该类中可以设置如何返回针对校验失败的 HTTP 响应。

# 注册与登录

这里我们开始讨论 UserDetailsService 的具体实现，首先我们定义用到的用户模型：

```java
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column
    private String username;
    @Column
    @JsonIgnore
    private String password;
    @Column
    private long salary;
    @Column
    private int age;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLES", joinColumns = {
            @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "ROLE_ID") })
    private Set<Role> roles;
}

@Data
@Entity
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column private String name;

  @Column private String description;
}
```

然后我们定义 UserServiceImpl，其继承了 UserDetailsService，提供了 loadUserByUsername 方法：

```java

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

  @Autowired private UserDAO userDao;

  @Autowired private BCryptPasswordEncoder bcryptEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userDao.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("Invalid username or password.");
    }
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), getAuthority(user));
  }

  private Set<SimpleGrantedAuthority> getAuthority(User user) {
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    user.getRoles()
        .forEach(
            role -> {
              // authorities.add(new SimpleGrantedAuthority(role.getName()));
              authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            });
    return authorities;
    // return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  // ...
}
```

这里 loadUserByUsername 检索到的用户会被填充到 User 对象中，并被添加到 SecurityContext 上下文中。

# 访问与鉴权

首先是用户的注册与登录：

```java

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private TokenProvider jwtTokenUtil;

  @Autowired private UserService userService;

  @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
  public ResponseEntity<?> sign_in(@RequestBody LoginUser loginUser)
      throws AuthenticationException {

    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginUser.getUsername(), loginUser.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    final String token = jwtTokenUtil.generateToken(authentication);
    return ResponseEntity.ok(new AuthToken(token));
  }

  @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
  public User saveUser(@RequestBody UserDTO user) {
    return userService.save(user);
  }
}

```

我们需要使用声明的编码器加密密码后放入数据库：

```java
@Override
public User save(UserDTO user) {
    User newUser = new User();
    newUser.setUsername(user.getUsername());
    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
    newUser.setAge(user.getAge());
    newUser.setSalary(user.getSalary());
    return userDao.save(newUser);
}
```

而在登录的时候：

```java
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(
        loginUser.getUsername(), loginUser.getPassword()));
```

authenticationManager 会根据传入的用户信息，调用 UserDetailsService 判断用户是否真实，然后创建 JWT 的 Token 并返回。注意，这里是把密码从数据库中读取出来，然后再次进行核对。最后在具体的接口访问，譬如在访问用户信息时，我们可以通过注解来指定某个接口的权限控制：

```java
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

  @Autowired private UserService userService;

  // @Secured({"ROLE_ADMIN", "ROLE_USER"})
  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public List<User> listUser() {
    return userService.findAll();
  }

  // @Secured("ROLE_USER")
  @PreAuthorize("hasRole('USER')")
  //// @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
  public User getOne(@PathVariable(value = "id") Long id) {
    return userService.findById(id);
  }
}
```

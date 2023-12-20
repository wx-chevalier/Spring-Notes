# WebSecurityConfig 配置

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

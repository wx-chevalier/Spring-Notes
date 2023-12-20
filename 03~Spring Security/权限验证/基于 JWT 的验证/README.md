# JWT

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

# Links

- 本文关联的代码仓库参阅：[Spring-Notes/examples](https://github.com/wx-chevalier/Spring-Notes)

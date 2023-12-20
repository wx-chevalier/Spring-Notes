# HTTP Basic Auth

HTTP Basic Auth 是较为简单的静态用户名密码认证方式，分别需要声明路由规则与配置 AuthenticationManagerBuilder, 本部分完整代码参考 [spring-security-basic-auth](https://github.com/wx-chevalier/Backend-Boilerplate/tree/master/java/spring/spring-basic-auth)。

```java
// 声明路由规则
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic();
}

// 声明权限验证
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .inMemoryAuthentication()
        .withUser("user")
        .password("password")
        .roles("USER")
        .and()
        .withUser("admin")
        .password("admin")
        .roles("USER", "ADMIN");
}
```

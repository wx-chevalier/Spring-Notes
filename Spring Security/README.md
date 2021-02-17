# Spring Security CheatSheet

- 用户登陆，会被 AuthenticationProcessingFilter 拦截，调用 AuthenticationManager 的实现，而且 AuthenticationManager 会调用 ProviderManager 来获取用户验证信息（不同的 Provider 调用的服务不同，因为这些信息可以是在数据库上，可以是在 LDAP 服务器上，可以是 xml 配置文件上等），如果验证通过后会将用户的权限信息封装一个 User 放到 spring 的全局缓存 SecurityContextHolder 中，以备后面访问资源时使用。

- 访问资源（即授权管理），访问 url 时，会通过 AbstractSecurityInterceptor 拦截器拦截，其中会调用 FilterInvocationSecurityMetadataSource 的方法来获取被拦截 url 所需的全部权限，在调用授权管理器 AccessDecisionManager，这个授权管理器会通过 spring 的全局缓存 SecurityContextHolder 获取用户的权限信息，还会获取被拦截的 url 和被拦截 url 所需的全部权限，然后根据所配的策略（有：一票决定，一票否定，少数服从多数等），如果权限足够，则返回，权限不够则报错并调用权限不足页面。

![image](https://user-images.githubusercontent.com/5803001/47625333-65bc1080-db5f-11e8-8971-ec4925c9b801.png)

在 Spring Boot 项目中引入 Spring Security 同样是简单的引入 starter 封装：

```java
// gradle
compile("org.springframework.boot:spring-boot-starter-security")

// maven
<dependencies>
    ...
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    ...
</dependencies>
```

然后声明 Web Security 相关的配置：

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ...
    }
    ...
}
```

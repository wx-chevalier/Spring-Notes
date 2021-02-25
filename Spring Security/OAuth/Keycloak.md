# 集成 Spring Security

之前，Spring Security OAuth 协议栈提供了将授权服务器设置为 Spring 应用的可能性。然后，我们必须将其配置为使用 JwtTokenStore，这样我们就可以使用 JWT 令牌。然而，OAuth 协议栈已经被 Spring 废弃，现在我们将使用 Keycloak 作为我们的授权服务器。所以这次，我们将把我们的授权服务器设置为 Spring Boot 应用中的嵌入式 Keycloak 服务器。它默认会发出 JWT 令牌，所以在这方面不需要任何其他配置。

# Resource Server

首先在 application.yml 中进行如下定义：

```yaml
server:
  port: 8081
  servlet:
    context-path: /resource-server

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/baeldung
          jwk-set-uri: http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs
```

JWTs 包括 Token 内的所有信息。因此，资源服务器需要验证 Token 的签名，以确保数据没有被修改。jwk-set-uri 属性包含了服务器可用于此目的的公钥。issuer-uri 属性指向基础授权服务器的 URI，它也可以用来验证 iss 声明，作为一种额外的安全措施。

此外，如果没有设置 jwk-set-uri 属性，资源服务器将尝试使用 issuer-ui 从授权服务器元数据端点确定该密钥的位置。重要的是，添加 issuer-uri 属性强制要求我们在启动 Resource Server 应用程序之前，应该先让 Authorization Server 运行。现在让我们看看如何使用 Java 配置来配置 JWT 支持。

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
              .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/user/info", "/api/foos/**")
                  .hasAuthority("SCOPE_read")
                .antMatchers(HttpMethod.POST, "/api/foos")
                  .hasAuthority("SCOPE_write")
                .anyRequest()
                  .authenticated()
            .and()
              .oauth2ResourceServer()
                .jwt();
    }
}
```

在这里，我们覆盖了默认的 Http 安全配置。因此，我们需要明确地指定，我们希望这是个资源服务器，并且我们将分别使用 oauth2ResourceServer()和 jwt()方法来使用 JWT 格式的访问令牌。上面的 JWT 配置是默认的 Spring Boot 实例为我们提供的。这也可以被定制，我们很快就会看到。

# Custom Claims in the Token

现在让我们设置一些基础设施，以便能够在授权服务器返回的访问令牌中添加一些自定义声明。框架提供的标准声明都是很好的，但大多数时候我们需要在令牌中添加一些额外的信息，以便在客户端使用。

让我们举一个自定义声明的例子，组织，它将包含一个给定用户的组织名称。

## Authorization Server Configuration

为此，我们需要在域定义文件 baeldung-realm.json 中添加一些配置：

# Todos

- https://www.baeldung.com/spring-security-oauth-jwt

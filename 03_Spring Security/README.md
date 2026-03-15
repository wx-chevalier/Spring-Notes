# Spring Security CheatSheet

Spring Security 是一个能够为基于 Spring 的企业应用系统提供声明式的安全访问控制解决方案的安全框架。它提供了一组可以在 Spring 应用上下文中配置的 Bean，充分利用了 Spring IoC（Inversion of Control 控制反转），DI（Dependency Injection 依赖注入）和 AOP（面向切面编程）功能，为应用系统提供声明式的安全访问控制功能，减少了为企业系统安全控制编写大量重复代码的工作。Spring Security 拥有以下特性：

- 对身份验证和授权的全面且可扩展的支持
- 防御会话固定、点击劫持，跨站请求伪造等攻击
- 支持 Servlet API 集成
- 支持与 Spring Web MVC 集成

目前 Spring Security 5 支持与以下技术进行集成：

- HTTP basic access authentication
- LDAP system
- OpenID identity providers
- JAAS API
- CAS Server
- ESB Platform
- ……
- Your own authentication system

在进入 Spring Security 正题之前，我们先来了解一下它的整体架构：

![Spring Security 结构](https://s3.ax1x.com/2021/02/24/yOxiJe.png)

# 核心组件

Spring Security 的核心处理过程如下：

- 用户登陆，会被 AuthenticationProcessingFilter 拦截，调用 AuthenticationManager 的实现，而且 AuthenticationManager 会调用 ProviderManager 来获取用户验证信息（不同的 Provider 调用的服务不同，因为这些信息可以是在数据库上，可以是在 LDAP 服务器上，可以是 xml 配置文件上等），如果验证通过后会将用户的权限信息封装一个 User 放到 Spring 的全局缓存 SecurityContextHolder 中，以备后面访问资源时使用。

- 访问资源（即授权管理），访问 url 时，会通过 AbstractSecurityInterceptor 拦截器拦截，其中会调用 FilterInvocationSecurityMetadataSource 的方法来获取被拦截 url 所需的全部权限，在调用授权管理器 AccessDecisionManager，这个授权管理器会通过 spring 的全局缓存 SecurityContextHolder 获取用户的权限信息，还会获取被拦截的 url 和被拦截 url 所需的全部权限，然后根据所配的策略（有：一票决定，一票否定，少数服从多数等），如果权限足够，则返回，权限不够则报错并调用权限不足页面。

## SecurityContextHolder，SecurityContext 和 Authentication

最基本的对象是 SecurityContextHolder，它是我们存储当前应用程序安全上下文的详细信息，其中包括当前使用应用程序的主体的详细信息。如当前操作的用户是谁，该用户是否已经被认证，他拥有哪些角色权限等。默认情况下，SecurityContextHolder 使用 ThreadLocal 来存储这些详细信息，这意味着 Security Context 始终可用于同一执行线程中的方法，即使 Security Context 未作为这些方法的参数显式传递。

## 获取当前用户的信息

因为身份信息与当前执行线程已绑定，所以可以使用以下代码块在应用程序中获取当前已验证用户的用户名：

```java
Object principal = SecurityContextHolder.getContext()
  .getAuthentication().getPrincipal();

if (principal instanceof UserDetails) {
  String username = ((UserDetails)principal).getUsername();
} else {
  String username = principal.toString();
}
```

调用 getContext() 返回的对象是 SecurityContext 接口的一个实例，对应 SecurityContext 接口定义如下：

```java
// org/springframework/security/core/context/SecurityContext.java
public interface SecurityContext extends Serializable {
	Authentication getAuthentication();
	void setAuthentication(Authentication authentication);
}
```

## Authentication

在 SecurityContext 接口中定义了 getAuthentication 和 setAuthentication 两个抽象方法，当调用 getAuthentication 方法后会返回一个 Authentication 类型的对象，这里的 Authentication 也是一个接口，它的定义如下：

```java
// org/springframework/security/core/Authentication.java
public interface Authentication extends Principal, Serializable {
  // 权限信息列表，默认是GrantedAuthority接口的一些实现类，通常是代表权限信息的一系列字符串。
	Collection<? extends GrantedAuthority> getAuthorities();
  // 密码信息，用户输入的密码字符串，在认证过后通常会被移除，用于保障安全。
	Object getCredentials();
	Object getDetails();
  // 最重要的身份信息，大部分情况下返回的是UserDetails接口的实现类，也是框架中的常用接口之一。
	Object getPrincipal();
	boolean isAuthenticated();
	void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
}
```

以上的 Authentication 接口是 spring-security-core jar 包中的接口，直接继承自 Principal 类，而 Principal 是位于 java.security 包中，由此可知 Authentication 是 spring security 中核心的接口。通过这个 Authentication 接口的实现类，我们可以得到用户拥有的权限信息列表，密码，用户细节信息，用户身份信息，认证信息等。

# Shiro

Apache Shiro 是一款 Java 安全框架，不依赖任何容器，可以运行在 Java SE 和 Java EE 项目中，它的主要作用是用来做身份认证、授权、会话管理和加密等操作。其实不用 Shiro，我们使用原生 Java API 就可以完成安全管理，很简单，使用过滤器去拦截用户的各种请求，然后判断是否登录、是否拥有某些权限即可。

我们完全可以完成这些操作，但是对于一个大型的系统，分散去管理编写这些过滤器的逻辑会比较麻烦，不成体系，所以需要使用结构化、工程化、系统化的解决方案。任何一个业务逻辑，一旦上升到企业级的体量，就必须考虑使用系统化的解决方案，也就是框架，否则后期的开发成本是相当巨大的，Shiro 就是来解决安全管理的系统化框架。

## 核心组件

Shiro 核心组件如下所示：

- UsernamePasswordToken，Shiro 用来封装用户登录信息，使用用户的登录信息创建令牌 Token，登录的过程即 Shiro 验证令牌是否具有合法身份以及相关权限。
- SecurityManager，Shiro 的核心部分，负责安全认证与授权。
- Subject，Shiro 的一个抽象概念，包含了用户信息。
- Realm，开发者自定义的模块，根据项目的需求，验证和授权的逻辑在 Realm 中实现。
- AuthenticationInfo，用户的角色信息集合，认证时使用。
- AuthorizationInfo，角色的权限信息集合，授权时使用。
- DefaultWebSecurityManager，安全管理器，开发者自定义的 Realm 需要注入到 DefaultWebSecurityManager 进行管理才能生效。
- ShiroFilterFactoryBean，过滤器工厂，Shiro 的基本运行机制是开发者定制规则，Shiro 去执行，具体的执行操作就是由 ShiroFilterFactoryBean 创建一个个 Filter 对象来完成。

Shiro 的运行机制如下图所示：

![Shiro 运行机制](https://s3.ax1x.com/2021/02/25/yjtXB4.png)

# 快速开始

Spring Boot 官方并没有纳入 Shiro，怎么解决？很简单，官方不提供支持，我们就自己手动在 pom.xml 中添加依赖，如下所示，我们全部选择最新版。

```xml
<!-- Shiro整合Spring -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.5.3</version>
</dependency>
```

## 自定义 Shiro 过滤器

对 URL 进行拦截，没有认证的需要认证，认证成功的则可以根据需要判断角色及权限。这个过滤器需要开发者自定义，然后去指定认证和授权的逻辑，继承抽象类 AuthorizingRealm，实现两个抽象方法分别完成授权和认证的逻辑。首先来完成认证的逻辑，需要连接数据库，这里我们使用 MyBatis Plus 来完成，pom.xml 中添加 MyBatis Plus 依赖，如下所示。

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.20</version>
</dependency>

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.3.1.tmp</version>
</dependency>
```

创建数据表 account，添加两条记录，SQL 如下所示。

```sql
CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `perms` varchar(20) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

LOCK TABLES `account` WRITE;
INSERT INTO `account` VALUES (1,'zs','123123','',''),(2,'ls','123123','manage',''),(3,'ww','123123','manage','administrator');
UNLOCK TABLES;
```

创建实体类 Account：

```java
@Data
public class Account {
    private Integer id;
    private String username;
    private String password;
    private String perms;
    private String role;
}
```

创建 AccountMapper 接口：

```java
public interface AccountMapper extends BaseMapper<Account> {
}
```

创建 application.yml

```js
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Drive
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

启动类添加 @MapperScan 注解扫描 Mapper 接口

```js
@SpringBootApplication
@MapperScan("com.southwind.springbootshirodemo.mapper")
public class SpringbootshirodemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootshirodemoApplication.class, args);
    }

}
```

首先通过单元测试调试 AccoutMapper 接口

```js
@SpringBootTest
class AccountMapperTest {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    void test(){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("username","user");
        Account account = accountMapper.selectOne(wrapper);
        System.out.println(account);
    }
}
```

![img](https://ask.qcloudimg.com/http-save/yehe-1646884/pv7089o8ip.png?imageView2/2/w/1620)

返回上图表示调试成功，MyBatis Plus 调试成功，接下来完成 Service 层代码编写。

```js
public interface AccountService {
    public Account findByUsername(String username);
}
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Account findByUsername(String username) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("username",username);
        return accountMapper.selectOne(wrapper);
    }
}
```

接下来回到 Shiro 完成用户认证，在 MyRealm 中完成代码的编写。

```js
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private AccountService accountService;

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        Account account = accountService.findByUsername(token.getUsername());
        if(account != null){
            return new SimpleAuthenticationInfo(account,account.getPassword(),getName());
        }
        return null;
    }
}
```

客户端传来的 username 和 password 会自动封装到 token，先根据 username 进行查询，如果返回 null，则表示用户名错误，直接 return null 即可，Shiro 会自动抛出 UnknownAccountException 异常。

如果返回不为 null，则表示用户名正确，再验证密码，直接返回 SimpleAuthenticationInfo 对象即可，如果密码验证成功，Shiro 认证通过，否则返回 IncorrectCredentialsException 异常。

自定义过滤器创建完成之后，需要进行配置才能生效，在 Spring Boot 应用中，不需要任何的 XML 配置，直接通过配置类进行装配，代码如下所示。

```js
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean filterFactoryBean(@Qualifier("manager") DefaultWebSecurityManager manager){
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(manager);
        return factoryBean;
    }


    @Bean
    public DefaultWebSecurityManager manager(@Qualifier("myRealm") MyRealm myRealm){
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm);
        return manager;
    }

    @Bean
    public MyRealm myRealm(){
        return new MyRealm();
    }
}
```

这个配置类中一共自动装配了 3 个 Bean 实例，第一个是自定义过滤器 MyRealm，我们的业务逻辑全部定义在这个 Bean 中。然后需要创建第二个 Bean 示例 DefaultWebSecurityManager，并且将 MyRealm 注入到 DefaultWebSecurityManager Bean 中，完成注册。

最终需要装配第三个 Bean ShiroFilterFactoryBean，这是 Shiro 自带的一个 Filter 工厂实例，所有的认证和授权判断都是由这个 Bean 生成的 Filter 对象来完成的，这就是 Shiro 框架的运行机制，开发者只需要定义规则，进行配置，具体的执行者全部由 Shiro 自己创建的 Filter 来完成。

所以我们需要给 ShiroFilterFactoryBean 实例注入认证及授权规则，如下所示。

认证过滤器：

- anon：无需认证即可访问，游客身份。
- authc：必须认证（登录）才能访问。
- authcBasic：需要通过 httpBasic 认证。
- user：不一定已通过认证，只要是曾经被 Shiro 记住过登录状态的用户就可以正常发起请求，比如 rememberMe。

授权过滤器:

- perms：必须拥有对某个资源的访问权限（授权）才能访问。
- role：必须拥有某个角色权限才能访问。
- port：请求的端口必须为指定值才可以访问。
- rest：请求必须是 RESTful，method 为 post、get、delete、put。
- ssl：必须是安全的 URL 请求，协议为 HTTPS。

比如，我们创建三个页面，main.html、manage.html、administrator.html，要求如下：

- 必须是登录状态才可以访问 main.html。
- 用户必须拥有 manage 授权才可以访问 manage.html。
- 用户必须拥有 administrator 角色才能访问 administrator.html。

代码如下所示。

```js
@Bean
public ShiroFilterFactoryBean filterFactoryBean(@Qualifier("manager") DefaultWebSecurityManager manager){
    ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
    factoryBean.setSecurityManager(manager);
    Map<String,String> map = new HashMap<>();
    map.put("/main","authc");
    map.put("/manage","perms[manage]");
    map.put("/administrator","roles[administrator]");
    factoryBean.setFilterChainDefinitionMap(map);
    //设置登录页面
    factoryBean.setLoginUrl("/login");
    //未授权页面
    factoryBean.setUnauthorizedUrl("/unauth");
    return factoryBean;
}
```

Controller 如下所示。

```js
@Controlle
public class MyController {

    @GetMapping("/{url}")
    public String redirect(@PathVariable("url") String url){
        return url;
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        try {
            subject.login(token);
            return "index";
        } catch (UnknownAccountException e) {
            model.addAttribute("msg","用户名错误");
            return "login";
        } catch (IncorrectCredentialsException e) {
            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }

    @RequestMapping("/unauth")
    @ResponseBody
    public String unauth(){
        return "未授权没有访问权限";
    }
}
```

现在只需要登录就可以访问 main.html，但是无法访问 manage.html，这是因为没有授权，接下来我们完成授权操作，回到 MyRealm，代码如下所示。

```js
@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    //获取当前登录对象
    Subject subject = SecurityUtils.getSubject();
    Account account = (Account) subject.getPrincipal();

    //设置角色
    Set<String> roles = new HashSet<>();
    roles.add(account.getRole());
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);

    //设置权限
    info.addStringPermission(account.getPerms());
    return info;
}
```

数据库如下所示：

zs 没有权限和角色，所以登录之后只能访问 main.html。

ls 拥有 manage 权限，没有角色，所以登录之后可以访问 main.html、manage.html。

ww 拥有 manage 权限和 administrator 角色，所以登录之后可以访问 main.html、manage.html、administrator.html。

## Shiro 整合 Thymeleaf

1、pom.xml 中引入依赖。

```js
<!-- Shiro整合Thymeleaf -->
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.0.0</version>
</dependency>
```

2、配置类添加 ShiroDialect。

```js
@Bean
public ShiroDialect shiroDialect(){
    return new ShiroDialect();
}
```

3、Controller 登录成功后将用户信息存入 session，同时添加退出操作。

```js
@PostMapping("/login")
public String login(String username, String password, Model model){
    Subject subject = SecurityUtils.getSubject();
    UsernamePasswordToken token = new UsernamePasswordToken(username,password);
    try {
        subject.login(token);
        Account account = (Account) subject.getPrincipal();
        subject.getSession().setAttribute("account",account);
        return "index";
    } catch (UnknownAccountException e) {
        model.addAttribute("msg","用户名错误");
        return "login";
    } catch (IncorrectCredentialsException e) {
        model.addAttribute("msg", "密码错误");
        return "login";
    }
}

@GetMapping("/logout")
public String logout(){
    Subject subject = SecurityUtils.getSubject();
    subject.logout();
    return "login";
}
```

4、index.html。

```js
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:shiro="http://www.thymeleaf.org/thymeleaf-extras-shiro">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="shortcut icon" href="#"/>
</head>
<body>
    <div th:if="${session.account == null}">
        <a href="/login">login</a>
    </div>
    <div th:if="${session.account != null}">
        欢迎回来！<span th:text="${session.account.username}"></span><a href="/logout">退出</a>
        <div>
            <a href="/main">main</a>
        </div>

        <div shiro:hasPermission="manage">
            <a href="/manage">manage</a>
        </div>

        <div shiro:hasRole="administrator">
            <a href="/administrator">administrator</a>
        </div>
    </div>
</body>
</html>
```

# Druid

Druid 是由阿里巴巴数据库事业部出品的开源项目。它除了是一个高性能数据库连接池之外，更是一个自带监控的数据库连接池。虽然 HikariCP 已经很优秀，但是对于国内用户来说，可能对于 Druid 更为熟悉。所以，对于如何在 Spring Boot 中使用 Druid 是后端开发人员必须要掌握的基本技能。

# 配置 Druid 数据源

在 pom.xml 中引入 druid 官方提供的 Spring Boot Starter 封装。

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.21</version>
</dependency>
```

在 application.properties 中配置数据库连接信息。Druid 的配置都以 spring.datasource.druid 作为前缀，所以根据之前的配置，稍作修改即可：

```java
spring.datasource.druid.url=jdbc:mysql://localhost:3306/test
spring.datasource.druid.username=root
spring.datasource.druid.password=
spring.datasource.druid.driver-class-name=com.mysql.cj.jdbc.Driver
```

配置 Druid 的连接池。与 Hikari 一样，要用好一个数据源，就要对其连接池做好相应的配置，比如下面这样：

```yml
spring.datasource.druid.initialSize=10
spring.datasource.druid.maxActive=20
spring.datasource.druid.maxWait=60000
spring.datasource.druid.minIdle=1
spring.datasource.druid.timeBetweenEvictionRunsMillis=60000
spring.datasource.druid.minEvictableIdleTimeMillis=300000
spring.datasource.druid.testWhileIdle=true
spring.datasource.druid.testOnBorrow=true
spring.datasource.druid.testOnReturn=false
spring.datasource.druid.poolPreparedStatements=true
spring.datasource.druid.maxOpenPreparedStatements=20
spring.datasource.druid.validationQuery=SELECT 1
spring.datasource.druid.validation-query-timeout=500
spring.datasource.druid.filters=stat
```

# 配置 Druid 监控

在 pom.xml 中引入 spring-boot-starter-actuator 模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

在 application.properties 中添加 Druid 的监控配置。

```yml
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
spring.datasource.druid.stat-view-servlet.reset-enable=true
spring.datasource.druid.stat-view-servlet.login-username=admin
spring.datasource.druid.stat-view-servlet.login-password=admin
```

上面的配置主要用于开启 stat 监控统计的界面以及监控内容的相关配置，具体释意如下：

- `spring.datasource.druid.stat-view-servlet.url-pattern`：访问地址规则
- `spring.datasource.druid.stat-view-servlet.reset-enable`：是否允许清空统计数据
- `spring.datasource.druid.stat-view-servlet.login-username`：监控页面的登录账户
- `spring.datasource.druid.stat-view-servlet.login-password`：监控页面的登录密码

针对之前实现的 UserService 内容，我们创建一个 Controller 来通过接口去调用数据访问操作：

```java
@Data
@AllArgsConstructor
@RestController
public class UserController {

    private UserService userService;

    @PostMapping("/user")
    public int create(@RequestBody User user) {
        return userService.create(user.getName(), user.getAge());
    }

    @GetMapping("/user/{name}")
    public List<User> getByName(@PathVariable String name) {
        return userService.getByName(name);
    }

    @DeleteMapping("/user/{name}")
    public int deleteByName(@PathVariable String name) {
        return userService.deleteByName(name);
    }

    @GetMapping("/user/count")
    public int getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/user/all")
    public int deleteAllUsers() {
        return userService.deleteAllUsers();
    }

}
```

完成上面所有配置之后，启动应用，访问 Druid 的监控页面 http://localhost:8080/druid/，输入上面 spring.datasource.druid.stat-view-servlet.login-username 和 spring.datasource.druid.stat-view-servlet.login-password 配置的登录账户与密码，就能看到如下监控页面：

![Druid Monitor](https://s3.ax1x.com/2021/02/07/ytze3R.png)

## 监控页面

进入到这边时候，就可以看到对于应用端而言的各种监控数据了。这里讲解几个最为常用的监控页面：

- 数据源：这里可以看到之前我们配置的数据库连接池信息以及当前使用情况的各种指标。

![数据源指标](https://s3.ax1x.com/2021/02/07/ytzgvq.md.png)

- SQL 监控：该数据源中执行的 SQL 语句极其统计数据。在这个页面上，我们可以很方便的看到当前这个 Spring Boot 都执行过哪些 SQL，这些 SQL 的执行频率和执行效率也都可以清晰的看到。如果你这里没看到什么数据？别忘了我们之前创建了一个 Controller，用这些接口可以触发 UserService 对数据库的操作。所以，这里我们可以通过调用接口的方式去触发一些操作，这样 SQL 监控页面就会产生一些数据：

![SQL 监控](https://s3.ax1x.com/2021/02/07/ytz5aF.png)

图中监控项上，执行时间、读取行数、更新行数都通过区间分布的方式表示，将耗时分布成 8 个区间：

- 0 - 1 耗时 0 到 1 毫秒的次数
- 1 - 10 耗时 1 到 10 毫秒的次数
- 10 - 100 耗时 10 到 100 毫秒的次数
- 100 - 1,000 耗时 100 到 1000 毫秒的次数
- 1,000 - 10,000 耗时 1 到 10 秒的次数
- 10,000 - 100,000 耗时 10 到 100 秒的次数
- 100,000 - 1,000,000 耗时 100 到 1000 秒的次数
- 1,000,000 - 耗时 1000 秒以上的次数

记录耗时区间的发生次数，通过区分分布，可以很方便看出 SQL 运行的极好、普通和极差的分布。 耗时区分分布提供了“执行+RS 时分布”，是将执行时间+ResultSet 持有时间合并监控，这个能方便诊断返回行数过多的查询。

- SQL 防火墙：该页面记录了与 SQL 监控不同维度的监控数据，更多用于对表访问维度、SQL 防御维度的统计。

![SQL 防火墙](https://s3.ax1x.com/2021/02/07/yNSpPH.md.png)

该功能数据记录的统计需要在`spring.datasource.druid.filters`中增加`wall`属性才会进行记录统计，比如这样：

```
spring.datasource.druid.filters=stat,wall
```

**注意**：这里的所有监控信息是对这个应用实例的数据源而言的，而并不是数据库全局层面的，可以视为应用层的监控，不可能作为中间件层的监控。

# TBD

- https://parg.co/kmH

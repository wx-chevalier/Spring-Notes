# Spring 数据库连接池

由于 Spring Boot 的自动化配置机制，大部分对于数据源的配置都可以通过配置参数的方式去改变。只有一些特殊情况，比如：更换默认数据源，多数据源共存等情况才需要去修改覆盖初始化的 Bean 内容。在 Spring Boot 自动化配置中，对于数据源的配置可以分为两类：

- 通用配置：以`spring.datasource.*`的形式存在，主要是对一些即使使用不同数据源也都需要配置的一些常规内容。比如：数据库链接地址、用户名、密码等。这里就不做过多说明了，通常就这些配置：

```java
spring.datasource.url=jdbc:mysql://localhost:3306/test
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

- 数据源连接池配置：以`spring.datasource.<数据源名称>.*`的形式存在，比如：Hikari 的配置参数就是`spring.datasource.hikari.*`形式。下面这个是我们最常用的几个配置项及对应说明：

```java
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.idle-timeout=500000
spring.datasource.hikari.max-lifetime=540000
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.connection-test-query=SELECT 1
```

这些配置的含义：

- `spring.datasource.hikari.minimum-idle`: 最小空闲连接，默认值 10，小于 0 或大于 maximum-pool-size，都会重置为 maximum-pool-size
- `spring.datasource.hikari.maximum-pool-size`: 最大连接数，小于等于 0 会被重置为默认值 10；大于零小于 1 会被重置为 minimum-idle 的值
- `spring.datasource.hikari.idle-timeout`: 空闲连接超时时间，默认值 600000（10 分钟），大于等于 max-lifetime 且 max-lifetime>0，会被重置为 0；不等于 0 且小于 10 秒，会被重置为 10 秒。
- `spring.datasource.hikari.max-lifetime`: 连接最大存活时间，不等于 0 且小于 30 秒，会被重置为默认值 30 分钟.设置应该比 mysql 设置的超时时间短
- `spring.datasource.hikari.connection-timeout`: 连接超时时间：毫秒，小于 250 毫秒，否则被重置为默认值 30 秒
- `spring.datasource.hikari.connection-test-query`: 用于测试连接是否可用的查询语句

## 手动创建连接池

现在，如果我们坚持使用 Spring Boot 的自动 DataSource 配置，并在当前状态下运行我们的项目，它就会像预期的那样工作。Spring Boot 将为我们完成所有繁重的基础架构工作。这包括创建一个 H2 DataSource 实现，它将由 HikariCP、Apache Tomcat 或 Commons DBCP 自动处理，并设置一个内存数据库实例。

此外，我们甚至不需要创建一个 application.properties 文件，因为 Spring Boot 也会提供一些默认的数据库设置。正如我们之前提到的，有时我们需要更高层次的定制，因此我们必须以编程的方式配置自己的 DataSource 实现。最简单的方法是定义一个 DataSource 工厂方法，并将其放置在用 @Configuration 注解的类中。

```java
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:test");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }
}
```

当然，我们也可以局部地应用 application.properties 中定义的属性：

```java
@Bean
public DataSource getDataSource() {
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.username("SA");
    dataSourceBuilder.password("");
    return dataSourceBuilder.build();
}

```

并在 application.properties 文件中额外指定一些：

```xml
spring.datasource.url=jdbc:h2:mem:test
spring.datasource.driver-class-name=org.h2.Driver
```

# TBD

- https://mp.weixin.qq.com/s/cgR-KVs1UKEM-xTEjIWKQg

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

# 多数据源配置

先在 Spring Boot 的配置文件 application.properties 中设置两个你要链接的数据库配置，比如这样：

```java
spring.datasource.primary.jdbc-url=jdbc:mysql://localhost:3306/test1
spring.datasource.primary.username=root
spring.datasource.primary.password=123456
spring.datasource.primary.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.secondary.jdbc-url=jdbc:mysql://localhost:3306/test2
spring.datasource.secondary.username=root
spring.datasource.secondary.password=123456
spring.datasource.secondary.driver-class-name=com.mysql.cj.jdbc.Driver
```

多数据源配置的时候，与单数据源不同点在于 spring.datasource 之后多设置一个数据源名称 primary 和 secondary 来区分不同的数据源配置，这个前缀将在后续初始化数据源的时候用到。数据源连接配置 2.x 和 1.x 的配置项是有区别的：2.x 使用 spring.datasource.secondary.jdbc-url，而 1.x 版本使用 spring.datasource.secondary.url。如果你在配置的时候发生了这个报错 java.lang.IllegalArgumentException: jdbcUrl is required with driverClassName.，那么就是这个配置项的问题。

完成多数据源的配置信息之后，就来创建个配置类来加载这些配置信息，初始化数据源，以及初始化每个数据源要用的 JdbcTemplate。你只需要在你的 Spring Boot 应用下添加下面的这个配置类即可完成！

```java
@Configuration
public class DataSourceConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

    @Bean
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return new JdbcTemplate(secondaryDataSource);
    }

}
```

前两个 Bean 是数据源的创建，通过@ConfigurationProperties 可以知道这两个数据源分别加载了 `spring.datasource.primary.*` 和 `spring.datasource.secondary.*` 的配置。
@Primary 注解指定了主数据源，就是当我们不特别指定哪个数据源的时候，就会使用这个 Bean 后两个 Bean 是每个数据源对应的 JdbcTemplate。可以看到这两个 JdbcTemplate 创建的时候，分别注入了 primaryDataSource 数据源和 secondaryDataSource 数据源。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class Chapter37ApplicationTests {

    @Autowired
    protected JdbcTemplate primaryJdbcTemplate;

    @Autowired
    protected JdbcTemplate secondaryJdbcTemplate;

    @Before
    public void setUp() {
        primaryJdbcTemplate.update("DELETE  FROM  USER ");
        secondaryJdbcTemplate.update("DELETE  FROM  USER ");
    }

    @Test
    public void test() throws Exception {
        // 往第一个数据源中插入 2 条数据
        primaryJdbcTemplate.update("insert into user(name,age) values(?, ?)", "aaa", 20);
        primaryJdbcTemplate.update("insert into user(name,age) values(?, ?)", "bbb", 30);

        // 往第二个数据源中插入 1 条数据，若插入的是第一个数据源，则会主键冲突报错
        secondaryJdbcTemplate.update("insert into user(name,age) values(?, ?)", "ccc", 20);

        // 查一下第一个数据源中是否有 2 条数据，验证插入是否成功
        Assert.assertEquals("2", primaryJdbcTemplate.queryForObject("select count(1) from user", String.class));

        // 查一下第一个数据源中是否有 1 条数据，验证插入是否成功
        Assert.assertEquals("1", secondaryJdbcTemplate.queryForObject("select count(1) from user", String.class));
    }

}
```

# TBD

- https://mp.weixin.qq.com/s/cgR-KVs1UKEM-xTEjIWKQg

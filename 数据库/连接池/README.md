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

## dynamic-datasource-spring-boot-starter

dynamic-datasource-spring-boot-starter 是一个基于 springboot 的快速集成多数据源的启动器。

1. 支持 **数据源分组** ，适用于多种场景 纯粹多库 读写分离 一主多从 混合模式。
2. 支持数据库敏感配置信息 **加密** ENC()。
3. 支持每个数据库独立初始化表结构 schema 和数据库 database。
4. 支持 **自定义注解** ，需继承 DS(3.2.0+)。
5. 提供对 Druid，Mybatis-Plus，P6sy，Jndi 的快速集成。
6. 简化 Druid 和 HikariCp 配置，提供 **全局参数配置** 。配置一次，全局通用。
7. 提供 **自定义数据源来源** 方案。
8. 提供项目启动后 **动态增加移除数据源** 方案。
9. 提供 Mybatis 环境下的 **纯读写分离** 方案。
10. 提供使用 **spel 动态参数** 解析数据源方案。内置 spel，session，header，支持自定义。
11. 支持 **多层数据源嵌套切换** 。（ServiceA >>> ServiceB >>> ServiceC）。
12. 提供对 shiro，sharding-jdbc,quartz 等第三方库集成的方案,注意事项和示例。
13. 提供 **基于 seata 的分布式事务方案。** 附：不支持原生 spring 事务。
14. 提供 **本地多数据源事务方案。** 附：不支持原生 spring 事务。

### 基础配置

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

从 2.0.0 开始所有数据源的 配置同级 ，不再有默认的主从限制，你可以给你的数据源起任何合适的名字。

```yml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #设置严格模式,默认false不启动. 启动后在未匹配到指定数据源时候会抛出异常,不启动则使用默认数据源.
      datasource:
        master:
          url: jdbc:mysql://xx.xx.xx.xx:3306/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver # 3.2.0开始支持SPI可省略此配置
        slave_1:
          url: jdbc:mysql://xx.xx.xx.xx:3307/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_2:
          url: ENC(xxxxx) # 内置加密,使用请查看详细文档
          username: ENC(xxxxx)
          password: ENC(xxxxx)
          driver-class-name: com.mysql.jdbc.Driver
          schema: db/schema.sql # 配置则生效,自动初始化表结构
          data: db/data.sql # 配置则生效,自动初始化数据
          continue-on-error: true # 默认true,初始化失败是否继续
          separator: ";" # sql默认分号分隔符


        #......省略
        #以上会配置一个默认库master，一个组slave下有两个子库slave_1,slave_2
```

多主多从方案：(谨慎使用，你清楚的知道多个主库间需要自己做同步)

```yml
# 多主多从                      纯粹多库（记得设置primary）                   混合配置
spring:                               spring:                               spring:
  datasource:                           datasource:                           datasource:
    dynamic:                              dynamic:                              dynamic:
      datasource:                           datasource:                           datasource:
        master_1:                             mysql:                                master:
        master_2:                             oracle:                               slave_1:
        slave_1:                              sqlserver:                            slave_2:
        slave_2:                              postgresql:                           oracle_1:
        slave_3:                              h2:                                   oracle_2:
```

@DS 可以注解在方法上或类上，同时存在就近原则方法上注解优先于类上注解：

```java
@Service
@DS("slave")
public class UserServiceImpl implements UserService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List selectAll() {
    return  jdbcTemplate.queryForList("select * from user");
  }

  @Override
  @DS("slave_1")
  public List selectByCondition() {
    return  jdbcTemplate.queryForList("select * from user where age >10");
  }
}
```

### 集成 Druid

SpringBoot2.x 默认使用 HikariCP，但在国内 Druid 的使用者非常庞大，此项目特地对其进行了适配，完成多数据源下使用 Druid 进行监控。主从可以使用不同的数据库连接池，如 master 使用 Druid 监控，从库使用 HikariCP。如果不配置连接池 type 类型，默认是 Druid 优先于 HikariCP。首先引入 druid-spring-boot-starter 依赖：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```

然后排除原生 Druid 的快速配置类，DruidDataSourceAutoConfigure 会注入一个 DataSourceWrapper，其会在原生的 spring.datasource 下找 url,username,password 等。而我们动态数据源的配置路径是变化的。

```java
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}

```

如果遇到 DruidDataSourceAutoConfigure 抛出 no suitable driver 表示注解排除没有生效尝试以下这种排除方法：

```yml
spring:
  autoconfigure:
    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure
```

其他属性依旧如原生 druid-spring-boot-starter 的配置：

```yml
spring:
  datasource:
    druid:
      stat-view-servlet:
        loginUsername: admin
        loginPassword: 123456
    dynamic:
      druid: # 2.2.3开始提供全局druid参数，以下是默认值和druid原生保持一致
        initial-size: 0
        max-active: 8
        min-idle: 2
        max-wait: -1
        min-evictable-idle-time-millis: 30000
        max-evictable-idle-time-millis: 30000
        time-between-eviction-runs-millis: 0
        validation-query: select 1
        validation-query-timeout: -1
        test-on-borrow: false
        test-on-return: false
        test-while-idle: true
        pool-prepared-statements: true
        max-open-prepared-statements: 100
        filters: stat,wall
        share-prepared-statements: true
      datasource:
        master:
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
          url: jdbc:mysql://47.100.20.186:3306/dynamic?characterEncoding=utf8&useSSL=false
          druid: # 以下参数针对每个库可以重新设置druid参数
            initial-size:
            max-active:
            min-idle:
            max-wait:
            min-evictable-idle-time-millis:
            max-evictable-idle-time-millis:
            time-between-eviction-runs-millis:
            validation-query: select 1 FROM DUAL #比如oracle就需要重新设置这个
            validation-query-timeout:
            test-on-borrow:
            test-on-return:
            test-while-idle:
            pool-prepared-statements:
            max-open-prepared-statements:
            filters:
            share-prepared-statements:
```

# TBD

- https://mp.weixin.qq.com/s/cgR-KVs1UKEM-xTEjIWKQg

# Spring Boot 参数配置

Spring Boot 针对我们常用的开发场景提供了一系列自动化配置来减少原本复杂而又几乎很少改动的模板化配置内容。但是，我们还是需要去了解如何在 Spring Boot 中修改这些自动化的配置内容，以应对一些特殊的场景需求，比如：我们在同一台主机上需要启动多个基于 Spring Boot 的 web 应用，若我们不为每个应用指定特别的端口号，那么默认的 8080 端口必将导致冲突。

# 程序中设置参数

在 `database.xml` 中可以这么写：

```xml
...
<bean name="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
p:driverClassName="com.mysql.jdbc.Driver"
p:url="${db.url}?useUnicode=true&amp;characterEncoding=utf-8&amp;allowMultiQueries=true"
p:username="${db.username}"
p:password="${db.password}">

</bean>
...
```

我们可以通过在启动应用程序时设置特定属性或通过自定义嵌入式服务器配置来以编程方式配置端口。

```java
@SpringBootApplication
public class CustomApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(CustomApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
    app.run(args);
  }
}
```

我们也可以直接设置实体类的属性，来修改端口号：

```java
@Component
public class ServerPortCustomizer
  implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

  @Override
  public void customize(ConfigurableWebServerFactory factory) {
    factory.setPort(8086);
  }
}
```

也可以使用 properties 属性：

```java
public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class).properties(properties()).run(args);
}

// 手动注入写死的配置信息
private static Properties properties() {
    Properties properties = new Properties();

    Locale locale = Locale.getDefault();
    properties.setProperty("locale.language", locale.getLanguage());
    properties.setProperty("locale.country", locale.getCountry());

    properties.setProperty("management.endpoints.web.exposure.include", "*");
    properties.setProperty("management.endpoints.web.exposure.include", "*");

    String druidPrefix = "spring.datasource";

    properties.setProperty(druidPrefix + ".type", "com.alibaba.druid.pool.DruidDataSource");
    properties.setProperty(druidPrefix + ".druid.max-active", "50");
    properties.setProperty(druidPrefix + ".druid.min-idle", "5");
    // properties.setProperty(druidPrefix + ".druid.remove-abandoned", "true");
    // properties.setProperty(druidPrefix + ".druid.remove-abandoned-timeout-millis", "120000");

    return properties;
}
```

# TBD

- https://mp.weixin.qq.com/s/e0tO2zogV-L6mXLfaiFCfw?from=groupmessage&isappinstalled=0 这样讲 SpringBoot 自动配置原理，你应该能明白了吧

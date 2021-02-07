# Spring Jdbc

对于信息的存储，现在已经有非常非常多的产品可以选择，其中不乏许多非常优秀的开源免费产品，比如：MySQL，Redis 等。接下来，我们将继续学习在使用 Spring Boot 开发服务端程序的时候，如何实现对各流行数据存储产品的增删改查操作。

# 基础使用

## 数据源配置

在我们访问数据库的时候，需要先配置一个数据源，下面分别介绍一下几种不同的数据库配置方式。首先，为了连接数据库需要引入 jdbc 支持，在 pom.xml 中引入如下配置：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

嵌入式数据库通常用于开发和测试环境，不推荐用于生产环境。Spring Boot 提供自动配置的嵌入式数据库有 H2、HSQL、Derby，你不需要提供任何连接配置就能使用。比如，我们可以在 pom.xml 中引入如下配置使用 HSQL：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <scope>runtime</scope>
</dependency>
```

以 MySQL 数据库为例，先引入 MySQL 连接的依赖包，在 pom.xml 中加入：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

在 src/main/resources/application.properties 中配置数据源信息：

```yml
spring.datasource.url=jdbc:mysql://localhost:3306/test
spring.datasource.username=dbuser
spring.datasource.password=dbpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

注意：因为 Spring Boot 2.1.x 默认使用了 MySQL 8.0 的驱动，所以这里采用 com.mysql.cj.jdbc.Driver，而不是老的 com.mysql.jdbc.Driver。

## 使用 JdbcTemplate 操作数据库

Spring 的 JdbcTemplate 是自动配置的，你可以直接使用@Autowired 或构造函数（推荐）来注入到你自己的 bean 中来使用。

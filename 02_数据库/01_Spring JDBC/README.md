# Spring Jdbc

对于信息的存储，现在已经有非常非常多的产品可以选择，其中不乏许多非常优秀的开源免费产品，比如：MySQL，Redis 等。接下来，我们将继续学习在使用 Spring Boot 开发服务端程序的时候，如何实现对各流行数据存储产品的增删改查操作。

# 基本概念

## JDBC

Java 数据库连接（Java Database Connectivity，简称 JDBC）是 Java 语言中用来规范客户端程序如何来访问数据库的应用程序接口，提供了诸如查询和更新数据库中数据的方法。JDBC 也是 Sun Microsystems 的商标。我们通常说的 JDBC 是面向关系型数据库的。JDBC API 主要位于 JDK 中的 java.sql 包中（之后扩展的内容位于 javax.sql 包中），主要包括（斜体代表接口，需驱动程序提供者来具体实现）：

- DriverManager：负责加载各种不同驱动程序（Driver），并根据不同的请求，向调用者返回相应的数据库连接（Connection）。
- Driver：驱动程序，会将自身加载到 DriverManager 中去，并处理相应的请求并返回相应的数据库连接（Connection）。
- Connection：数据库连接，负责与进行数据库间通讯，SQL 执行以及事务处理都是在某个特定 Connection 环境中进行的。可以产生用以执行 SQL 的 Statement。
- Statement：用以执行 SQL 查询和更新（针对静态 SQL 语句和单次执行）。PreparedStatement：用以执行包含动态参数的 SQL 查询和更新（在服务器端编译，允许重复执行以提高效率）。
- CallableStatement：用以调用数据库中的存储过程。
- SQLException：代表在数据库连接的建立和关闭和 SQL 语句的执行过程中发生了例外情况（即错误）。

## 数据源

可以看到，在 java.sql 中并没有数据源（Data Source）的概念。这是由于在 java.sql 中包含的是 JDBC 内核 API，另外还有个 javax.sql 包，其中包含了 JDBC 标准的扩展 API。而关于数据源（Data Source）的定义，就在 javax.sql 这个扩展包中。实际上，在 JDBC 内核 API 的实现下，就已经可以实现对数据库的访问了，那么我们为什么还需要数据源呢？主要出于以下几个目的：

- 封装关于数据库访问的各种参数，实现统一管理
- 通过对数据库的连接池管理，节省开销并提高效率

在 Java 这个自由开放的生态中，已经有非常多优秀的开源数据源可以供大家选择，比如：DBCP、C3P0、Druid、HikariCP 等。

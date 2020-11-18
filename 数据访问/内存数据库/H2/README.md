# H2

H2 是一个用 Java 开发的嵌入式数据库，它本身只是一个类库，即只有一个 jar 文件，可以直接嵌入到应用项目中。H2 主要有如下三个用途：

- 第一个用途，也是最常使用的用途就在于可以同应用程序打包在一起发布，这样可以非常方便地存储少量结构化数据。

- 第二个用途是用于单元测试。启动速度快，而且可以关闭持久化功能，每一个用例执行完随即还原到初始状态。

- 第三个用途是作为缓存，即当做内存数据库，作为 NoSQL 的一个补充。当某些场景下数据模型必须为关系型，可以拿它当 Memcached 使，作为后端 MySQL/Oracle 的一个缓冲层，缓存一些不经常变化但需要频繁访问的数据，比如字典表、权限表。

# 连接方式

H2 支持以下三种连接模式：

- 嵌入式模式（使用 JDBC 的本地连接）

- 服务器模式（使用 JDBC 或 ODBC 在 TCP/IP 上的远程连接）

- 混合模式（本地和远程连接同时进行）

## 嵌入式模式

在嵌入式模式下，应用程序使用 JDBC 从同一 JVM 中打开数据库。这是最快也是最容易的连接方式。缺点是数据库可能只在任何时候在一个虚拟机（和类加载器）中打开。与所有模式一样，支持持久性和内存数据库。对并发打开数据库的数量或打开连接的数量没有限制。

![Embedded](https://s2.ax1x.com/2019/12/16/Q5K9PI.png)

## 服务器模式

当使用服务器模式（有时称为远程模式或客户机/服务器模式）时，应用程序使用 JDBC 或 ODBC API 远程打开数据库。服务器需要在同一台或另一台虚拟机上启动，或者在另一台计算机上启动。许多应用程序可以通过连接到这个服务器同时连接到同一个数据库。在内部，服务器进程在嵌入式模式下打开数据库。

服务器模式比嵌入式模式慢，因为所有数据都通过 TCP/IP 传输。与所有模式一样，支持持久性和内存数据库。对每个服务器并发打开的数据库数量或打开连接的数量没有限制。

![服务器模式](https://s2.ax1x.com/2019/12/16/Q5KZZQ.png)

## 混合模式

混合模式是嵌入式和服务器模式的结合。连接到数据库的第一个应用程序在嵌入式模式下运行，但也启动服务器，以便其他应用程序（在不同进程或虚拟机中运行）可以同时访问相同的数据。本地连接的速度与数据库在嵌入式模式中的使用速度一样快，而远程连接速度稍慢。

服务器可以从应用程序内（使用服务器 API）启动或停止，或自动（自动混合模式）。当使用自动混合模式时，所有想要连接到数据库的客户端（无论是本地连接还是远程连接）都可以使用完全相同的数据库 URL 来实现。

![混合模式](https://s2.ax1x.com/2019/12/16/Q5KKGq.png)

# Spring Boot 中使用

## 数据准备

我们要创建两个 Sql 文件，以便项目启动的时候，将表结构和数据初始化到数据库。表结构文件（schema-h2.sql）内容：

```sql
DROP TABLE IF EXISTS user;

CREATE TABLE user
(
	id BIGINT(20) NOT NULL COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	PRIMARY KEY (id)
);
```

表数据文件（data-h2.sql）内容：

```sql
INSERT INTO user (id, name, age, email) VALUES
(1, 'neo', 18, 'smile1@ityouknow.com'),
(2, 'keep', 36, 'smile2@ityouknow.com'),
(3, 'pure', 28, 'smile3@ityouknow.com'),
(4, 'smile', 21, 'smile4@ityouknow.com'),
(5, 'it', 24, 'smile5@ityouknow.com');
```

在示例项目的 resources 目录下创建 db 文件夹，将两个文件放入其中。

## 添加依赖

添加相关依赖包，pom.xml 中的相关依赖内容如下：

```xml
<dependencies>
	<dependency>
		<groupId>com.h2database</groupId>
		<artifactId>h2</artifactId>
		<scope>runtime</scope>
	</dependency>
</dependencies>
```

然后配置文件如下：

```yml
# DataSource Config
spring:
  datasource:
    driver-class-name: org.h2.Driver
    schema: classpath:db/schema-h2.sql
    data: classpath:db/data-h2.sql
    url: jdbc:h2:mem:test
    username: root
    password: test

# Logger Config
logging:
  level:
    wx: debug
```

# Flyway

Flyway 是一个简单开源数据库版本控制器（约定大于配置），主要提供 migrate、clean、info、validate、baseline、repair 等命令。它支持 SQL（PL/SQL、T-SQL）方式和 Java 方式，支持命令行客户端等，还提供一系列的插件支持（Maven、Gradle、SBT、ANT 等）。

# 快速开始

在 start.spring.io 上新建一个 SpringBoot 工程，要求能连上自己本地新建的 mysql 数据库 flyway；但要注意的是，application.properties 中数据库的配置务必配置正确，下述步骤中系统启动时，flyway 需要凭借这些配置连接到数据库：

```conf
# db config
spring.datasource.url=jdbc:mysql://localhost:3306/flyway?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

首先，在 pom 文件中引入 flyway 的核心依赖包：

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>5.2.4</version>
</dependency>
```

其次，在 src/main/resources 目录下面新建 db.migration 文件夹，默认情况下，该目录下的.sql 文件就算是需要被 flyway 做版本控制的数据库 SQL 语句。但是此处的 SQL 语句命名需要遵从一定的规范，否则运行的时候 flyway 会报错。命名规则主要有两种：

- 仅需要被执行一次的 SQL 命名以大写的"V"开头，后面跟上"0~9"数字的组合,数字之间可以用“.”或者下划线"\_"分割开，然后再以两个下划线分割，其后跟文件名称，最后以.sql 结尾。比如，`V2.1.5__create_user_ddl.sql、V4.1_2__add_user_dml.sql`。
- 可重复运行的 SQL，则以大写的“R”开头，后面再以两个下划线分割，其后跟文件名称，最后以.sql 结尾。比如，`R__truncate_user_dml.sql`。

其中，V 开头的 SQL 执行优先级要比 R 开头的 SQL 优先级高。如下，我们准备了三个脚本，分别为：

- `V1__create_user.sql`，其中代码如下，目的是建立一张 user 表，且只执行一次。

```sql
CREATE TABLE IF NOT EXISTS `USER`(
    `USER_ID`          INT(11)           NOT NULL AUTO_INCREMENT,
`USER_NAME`        VARCHAR(100)      NOT NULL COMMENT '用户姓名',
`AGE`              INT(3)            NOT NULL COMMENT '年龄',
`CREATED_TIME`     datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP,
`CREATED_BY`       varchar(100)      NOT NULL DEFAULT 'UNKNOWN',
`UPDATED_TIME`     datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP,
`UPDATED_BY`       varchar(100)      NOT NULL DEFAULT 'UNKNOWN',
PRIMARY KEY (`USER_ID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

- `V2__add_user.sql`，其中代码如下，目的是往 user 表中插入一条数据，且只执行一次。

```sql
insert into `user`(user_name,age) values('lisi',33);
```

- `R__add_unknown_user.sql`，其中代码如下，目的是每次启动倘若有变化，则往 user 表中插入一条数据。

```sql
insert into `user`(user_name,age) values('unknown',33);
```

![文件目录](https://pic.imgdb.cn/item/61a2f3cf2ab3f51d91844a8d.jpg)

其中 2.1.6、2.1.7 和 every 的文件夹不会影响 flyway 对 SQL 的识别和运行，可以自行取名和分类。执行 Flyway Migrate 指令，可以看到会生成如下的表：

![flyway_schema_history](https://pic.imgdb.cn/item/61a2f42b2ab3f51d9184a783.jpg)

而且，user 表也已经创建好了并插入了两条数据：

![user 表](https://pic.imgdb.cn/item/61a2f44a2ab3f51d9184c693.jpg)

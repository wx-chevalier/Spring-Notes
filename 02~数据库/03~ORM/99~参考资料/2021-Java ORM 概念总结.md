## 什么是 ORM？

ORM，即对象关系映射（**O**bject **R**elational **M**apping）模式

在初学 Java 的时候，都是使用 JDBC 方式连接数据库。之后逐步使用 EclipseLink、iBATIS（半自动） 和 Hibernate（全自动）等开源 ORM 框架。

JDBC 的使用流程为：

1. 加载数据库驱动 （JDBC Driver）
2. 创建数据库链接
3. 创建编译对象（预编译对象 PrepareStatement）
4. 设置入参执行 SQL
5. 返回结果集 (resultSet)

![image-20230319134837722](https://assets.ng-tech.icu/item/20230514224404.png)

JDBC 使用流程

![image-20230312192006619](https://assets.ng-tech.icu/item/20230514224423.png)

使用 JDBC 连接数据库

**ORM 框架主要可以解决面向对象与关系数据库之间互不匹配的问题，即用于处理面向对象编程语言中不同类型系统间的数据转换**

### [#](https://www.bantanger.fun/pages/5f5e77/#jdbc-的缺点)JDBC 的缺点

- 硬编码 --> 反射，封装，代理
- 频繁释放数据库连接资源 --> 连接池

![image-20230319140832760](https://cdn.staticaly.com/gh/BanTanger/image-hosting@master/00.%E6%80%BB%E7%BB%93-assets/202303191408128.png)

### [#](https://www.bantanger.fun/pages/5f5e77/#为什么会出现-orm-思想)为什么会出现 ORM 思想

先从项目中数据流存储形式这个角度说起.简单拿 MVC 这种分层模式来说，Model 作为数据承载实体. 在用户界面层和业务逻辑层之间数据实现面向对象 OO 形式传递. 当我们需要通过 Control 层分发请求把数据持久化时我们会发现. 内存中的面向对象的 **OO** 如何**持久化成**关系型数据中存储**一条实际数据记录**呢？

面向对象是从软件工程基本原则(如耦合、聚合、封装)的基础上发展起来的，而关系数据库则是从数学理论发展而来的。两者之间是不匹配的。而 ORM 作为项目中间件形式实现数据在不同场景下数据关系映射。**对象关系映射（Object Relational Mapping，简称 ORM）是一种为了解决面向对象与关系数据库存在的互不匹配的现象的技术**，ORM 就是这样而来的.

ORM 是连接数据库的桥梁，只要提供了持久化类与表的映射关系，ORM 框架在运行是就能**参照映射文件的信息将对象持久化到数据库中**

它的作用就是在关系型数据库和业务实体对象之间做**一层映射**，这样在具体操作业务对象时，就不需要和复杂的 SQL 语句打交道，而只需简单操作对象的属性和方法

![在这里插入图片描述](https://cdn.staticaly.com/gh/BanTanger/image-hosting@master/00.%E6%80%BB%E7%BB%93-assets/202303121549216.png)

### [#](https://www.bantanger.fun/pages/5f5e77/#优缺点)优缺点

优点：

1. 隐藏数据访问细节，封闭的进行交互。
2. 构造固化数据结构简单。

缺点：

1. 自动化意味着映射和关联管理，代价是牺牲性能（现在 ORM 采用各种方法减轻这块，LazyLoad Cache）
2. 面对复杂查询，ORM 力不从心

### [#](https://www.bantanger.fun/pages/5f5e77/#orm-框架和-mybatis-的区别)ORM 框架和 MyBatis 的区别

ORM 框架：将数据库表一行对应一个类实例。对类的操作会影响到数据库

MyBatis：将查询语句所得到的 ResultSet 映射成类，在使用数据库时需要写 SQL 语句，对类的修改不会影响到数据库

参考:

[什么是 ORM，设计架构(opens new window)](https://zhuanlan.zhihu.com/p/486987053)

## [#](https://www.bantanger.fun/pages/5f5e77/#orm-框架设计)ORM 框架设计

ORM 框架主要通过**参数映射、SQL 解析和执行，以及结果映射**的方式对数据库进行操作

![image-20230312162050029](https://cdn.staticaly.com/gh/BanTanger/image-hosting@master/00.%E6%80%BB%E7%BB%93-assets/202303121620277.png)

ORM 框架实现的核心类包括加载配置文件、解析 XML 文件、获取数据库 Session、操作数据库以及返回结果

![image-20230312165147079](https://cdn.staticaly.com/gh/BanTanger/image-hosting@master/00.%E6%80%BB%E7%BB%93-assets/202303121651695.png)

- SqlSession 时对数据库进行定义和处理的类，包括常用的方法，如 selectOne、selectList 等
- DefaultSqlSessionFactory 对数据库配置的开启绘画的工厂处理类，这里的工厂会操作 DefaultSqlSession
- SqlSessionFactoryBuilder 是对数据库进行操作的核心类，包括处理工厂、解析解析 和获取会话

## [#](https://www.bantanger.fun/pages/5f5e77/#如果让你实现一个-mybatis-应该怎么设计)如果让你实现一个 MyBatis ，应该怎么设计？

![手写MyBatis-第 3 页.drawio](https://cdn.staticaly.com/gh/BanTanger/image-hosting@master/00.%E6%80%BB%E7%BB%93-assets/202303211639742.png)

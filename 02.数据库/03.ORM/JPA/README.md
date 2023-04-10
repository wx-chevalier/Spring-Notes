# JPA

Java 持久化 API（JPA）是一个独立于供应商的、用于映射 Java 对象和关系型数据库表的规范。此规范的实现，使得应用程序的开发者们可以不依赖于他们工作中面对的特定数据库产品，从而开发出可以与不同数据库产品良好工作的 CRUD（创建、读取、更新、删除）操作代码。这些框架除了可以用于处理与数据库交互的代码（JDBC 代码），也可以用于映射数据和应用程序中的对象。

JPA 由三个不同的组件构成：

- `实体（Entities）`: 在当前版本的 JPA 中实体是普通 Java 对象（POJO）。老版本的 JPA 中实体类需要继承 JPA 提供的实体基类，但是这样的设计导致框架中存在了严重的依赖关系，测试变得更加困难；所以在新版 JPA 中不再要求实体类继承任何框架类。
- `对象-关系型元数据（Object-relational metadata）`: 应用程序的开发者们必须正确设定 Java 类和它们的属性与数据库中的表和列的映射关系。有两种设定方式：通过特定的配置文件建立映射；或者使用在新版本中支持的注解。
- `Java持久化查询语句（Java Persistence Query Language - JPQL):` 因为 JPA 旨在建立不依赖于特定的数据库的抽象层，所以它也提供了一种专有查询语言来代替 SQL。这种由 JPQL 到 SQL 语言的转换，为 JPA 提供了支持不同数据库方言的特性，使得开发者们在实现查询逻辑时不需要考虑特定的数据库类型。

# 实体生命周期

描述了实体对象从创建到受控、从删除到游离的状态变换。对实体的操作主要就是改变实体的状态。

![实体生命周期](https://s3.ax1x.com/2021/02/07/yNu46f.png)

- New，新创建的实体对象，没有主键(identity)值
- Managed，对象处于 Persistence Context(持久化上下文）中，被 EntityManager 管理
- Detached，对象已经游离到 Persistence Context 之外，进入 Application Domain
- Removed, 实体对象被删除

EntityManager 提供一系列的方法管理实体对象的生命周期，包括：

- persist, 将新创建的或已删除的实体转变为 Managed 状态，数据存入数据库。
- remove，删除受控实体
- merge，将游离实体转变为 Managed 状态，数据存入数据库。

如果使用了事务管理，则事务的 commit/rollback 也会改变实体的状态。

# 实体关系映射（ORM）

![实体关系映射](https://s3.ax1x.com/2021/02/07/yNKFhR.png)

## ID 生成策略

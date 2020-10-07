![Mybatis Plus 概念图](https://s2.ax1x.com/2019/12/16/Q4ASun.png)

# Mybatis Plus

MyBatis-Plus（简称 MP）是一个 MyBatis 的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。

![MyBatis Plus 架构](https://s2.ax1x.com/2019/12/16/Q4Ayvj.md.png)

# 背景特性

- 无侵入：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
- 损耗小：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
- 强大的 CRUD 操作：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器
- 支持 Lambda 形式调用：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
- 支持多种数据库：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer2005、SQLServer 等多种数据库
- 支持主键自动生成：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
- 支持 XML 热加载：Mapper 对应的 XML 支持热加载，对于简单的 CRUD 操作，甚至可以无 XML 启动
- 支持 ActiveRecord 模式：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
- 支持自定义全局通用操作：支持全局通用方法注入（Write once, use anywhere）
- 支持关键词自动转义：支持数据库关键词（order、key……）自动转义，还可自定义关键词
- 内置代码生成器：采用代码或者 Maven 插件可快速生成 Mapper 、Model 、Service 、Controller 层代码，支持模板引擎，有超多自定义配置等
- 内置分页插件：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
- 内置性能分析插件：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
- 内置全局拦截插件：提供全表 delete 、update 操作智能分析阻断，也可自定义拦截规则，预防误操作
- 内置 Sql 注入剥离器：支持 Sql 注入剥离，有效预防 Sql 注入攻击

# 快速开始

- 添加依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.1.1</version>
</dependency>
```

- 继承通用接口

```java
public interface UserMapper extends BaseMapper<User> {}
```

- 查询

```java
List<User> userList = userMapper
    .selectList(
        new QueryWrapper<User>()
            .lambda()
            .ge(User::getAge, 18)
    );
```

MyBatis-Plus 将会生成以下查询 SQL：

```sql
SELECT * FROM user WHERE age >= 18
```

# 案例：用户操作

创建 MybatisPlusConfig 类，指定 Mapper 地址，启用分页功能。

```java
@Configuration
@MapperScan("wx.mapper")
public class MybatisPlusConfig {

  /**
   * 分页插件
   */
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
  }
}
```

创建实体类 User

```java
@Data
public class User {
  private Long id;
  private String name;
  private Integer age;
  private String email;
}
```

`@Data` 为 lombok 语法，自动注入 getter/setter 方法。接下来创建对象对于的 Mapper。

```java
public interface UserMapper extends BaseMapper<User> {}
```

## 测试

创建 MyBatisPlusTest 类，注入上面创建的 UserMapper 类。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisPlusTest {
  @Autowired
  private UserMapper userMapper;
}
```

- 测试查询单挑数据，并输出

```java
@Test
public void testSelectOne() {
    User user = userMapper.selectById(1L);
    System.out.println(user);
}
```

- 测试添加数据

```java
@Test
public void testInsert() {
    User user = new User();
    user.setName("微笑");
    user.setAge(3);
    user.setEmail("neo@tooool.org");
    assertThat(userMapper.insert(user)).isGreaterThan(0);
    // 成功直接拿会写的 ID
    assertThat(user.getId()).isNotNull();
}
```

assertThat() 是 Assert 的一个精通方法，用来比对返回结果，包来自 `import static org.assertj.core.api.Assertions.assertThat;`。

- 测试删除数据

```java
@Test
public void testDelete() {
    assertThat(userMapper.deleteById(3L)).isGreaterThan(0);
    assertThat(userMapper.delete(new QueryWrapper<User>()
            .lambda().eq(User::getName, "smile"))).isGreaterThan(0);
}
```

`QueryWrapper` 是 MyBatis-Plus 内部辅助查询类，可以使用 lambda 语法，也可以不使用。利用 QueryWrapper 类可以构建各种查询条件。

- 测试更新数据

```java
@Test
public void testUpdate() {
    User user = userMapper.selectById(2);
    assertThat(user.getAge()).isEqualTo(36);
    assertThat(user.getName()).isEqualTo("keep");

    userMapper.update(
            null,
            Wrappers.<User>lambdaUpdate().set(User::getEmail, "123@123").eq(User::getId, 2)
    );
    assertThat(userMapper.selectById(2).getEmail()).isEqualTo("123@123");
}
```

- 测试查询所有数据

```java
@Test
public void testSelect() {
    List<User> userList = userMapper.selectList(null);
    Assert.assertEquals(5, userList.size());
    userList.forEach(System.out::println);
}
```

- 测试非分页查询

```java
@Test
public void testPage() {
    System.out.println("----- baseMapper 自带分页 ------");
    Page<User> page = new Page<>(1, 2);
    IPage<User> userIPage = userMapper.selectPage(page, new QueryWrapper<User>()
            .gt("age", 6));
    assertThat(page).isSameAs(userIPage);
    System.out.println("总条数 ------> " + userIPage.getTotal());
    System.out.println("当前页数 ------> " + userIPage.getCurrent());
    System.out.println("当前每页显示数 ------> " + userIPage.getSize());
    print(userIPage.getRecords());
    System.out.println("----- baseMapper 自带分页 ------");
}
```

查询大于 6 岁的用户，并且分页展示，每页两条数据，展示第一页。

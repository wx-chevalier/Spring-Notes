# MyBatis

MyBatis 是支持普通 SQL 查询，存储过程和高级映射的优秀持久层框架，它提供一种半自动化的 ORM 实现。这里的“半自动化”，是相对 Hibernate 等提供了全面的数据库封装机制的“全自动化”ORM 实现而言，全自动 ORM 实现了 POJO 和数据库表之间的映射，以及 SQL 的自动生成和执行。MyBatis 消除了几乎所有的 JDBC 代码和参数的手工设置以及对结果集的检索。MyBatis 可以使用简单的 XML 或注解用于配置和原始映射，将接口和 Java 的 POJO（Plain Old Java Objects，普通的 Java 对象）映射成数据库中的记录。

MyBatis 允许将 SQL 写在 XML 中，便于统一的管理与优化，并且与程序代码解耦合。同时 MyBatis 提供了映射标签，支持对象与数据库 ORM 字段关系映射，支持编写动态 SQL。不过 MyBatis 也存在不少的痛点，由于 XML 里标签 ID 必须唯一，导致 DAO 中的方法不支持重载，并且 DAO 层过于简单，仍然需要大量对象组装的工作量。同时字段映射标签和对象关系映射标签仅仅是对映射关系的描述，具体实现仍然依赖于 SQL；譬如配置了一对多的 Collection 标签之后，如果 SQL 中没有 Join 子查询或者查询子表的话，查询后返回的对象是不具备对象关系的，即 Collection 的对象为 null。

# 快速开始

新建 Spring Boot 项目，在 pom.xml 中引入 MyBatis 的 Starter 以及 MySQL Connector 依赖，具体如下：

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.1</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

关于 mybatis-spring-boot-starter 的版本需要注意：

- `2.1.x`版本适用于：MyBatis 3.5+、Java 8+、Spring Boot 2.1+
- `2.0.x`版本适用于：MyBatis 3.5+、Java 8+、Spring Boot 2.0/2.1
- `1.3.x`版本适用于：MyBatis 3.4+、Java 6+、Spring Boot 1.5

同之前介绍的使用 jdbc 模块和 jpa 模块连接数据库一样，在 application.properties 中配置 mysql 的连接配置：

```yml
spring.datasource.url=jdbc:mysql://localhost:3306/test
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Mysql 中创建一张用来测试的表，比如：User 表，其中包含 id(BIGINT)、age(INT)、name(VARCHAR)字段。

```sql
CREATE TABLE `User` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
```

创建 User 表的映射对象 User：

```java
@Data
@NoArgsConstructor
public class User {

    private Long id;

    private String name;
    private Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```

创建 User 表的操作接口：UserMapper。在接口中定义两个数据操作，一个插入，一个查询，用于后续单元测试验证。

```java
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insert(@Param("name") String name, @Param("age") Integer age);

}
```

创建 Spring Boot 主类：

```java
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

创建单元测试。具体测试逻辑如下：

- 插入一条 name=AAA，age=20 的记录，然后根据 name=AAA 查询，并判断 age 是否为 20
- 测试结束回滚数据，保证测试单元每次运行的数据环境独立

```java
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Rollback
    public void test() throws Exception {
        userMapper.insert("AAA", 20);
        User u = userMapper.findByName("AAA");
        Assert.assertEquals(20, u.getAge().intValue());
    }

}
```

## 注解配置说明

下面通过几种不同传参方式来实现前文中实现的插入操作，来学习一下 MyBatis 中常用的一些注解。

- 使用 @Param

在之前的整合示例中我们已经使用了这种最简单的传参方式，如下：

```java
@Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
int insert(@Param("name") String name, @Param("age") Integer age);
```

这种方式很好理解，@Param 中定义的 name 对应了 SQL 中的#{name}，age 对应了 SQL 中的#{age}。

- 使用 Map

如下代码，通过 `Map<String, Object>` 对象来作为传递参数的容器：

```java
@Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER})")
int insertByMap(Map<String, Object> map);
```

对于 Insert 语句中需要的参数，我们只需要在 map 中填入同名的内容即可，具体如下面代码所示：

```java
Map<String, Object> map = new HashMap<>();
map.put("name", "CCC");
map.put("age", 40);
userMapper.insertByMap(map);
```

- 使用对象

除了 Map 对象，我们也可直接使用普通的 Java 对象来作为查询条件的传参，比如我们可以直接使用 User 对象:

```java
@Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
int insertByUser(User user);
```

这样语句中的#{name}、#{age}就分别对应了 User 对象中的 name 和 age 属性。

## 增删改查

MyBatis 针对不同的数据库操作分别提供了不同的注解来进行配置，在之前的示例中演示了@Insert，下面针对 User 表做一组最基本的增删改查作为示例：

```java
public interface UserMapper {

    @Select("SELECT * FROM user WHERE name = #{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO user(name, age) VALUES(#{name}, #{age})")
    int insert(@Param("name") String name, @Param("age") Integer age);

    @Update("UPDATE user SET age=#{age} WHERE name=#{name}")
    void update(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    void delete(Long id);
}
```

在完成了一套增删改查后，不妨我们试试下面的单元测试来验证上面操作的正确性：

```java
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private UserMapper userMapper;

	@Test
	@Rollback
	public void testUserMapper() throws Exception {
		// insert一条数据，并select出来验证
		userMapper.insert("AAA", 20);
		User u = userMapper.findByName("AAA");
		Assert.assertEquals(20, u.getAge().intValue());
		// update一条数据，并select出来验证
		u.setAge(30);
		userMapper.update(u);
		u = userMapper.findByName("AAA");
		Assert.assertEquals(30, u.getAge().intValue());
		// 删除这条数据，并select验证
		userMapper.delete(u.getId());
		u = userMapper.findByName("AAA");
		Assert.assertEquals(null, u);
	}
}
```

## 返回结果绑定

对于增、删、改操作相对变化较小。而对于“查”操作，我们往往需要进行多表关联，汇总计算等操作，那么对于查询的结果往往就不再是简单的实体对象了，往往需要返回一个与数据库实体不同的包装类，那么对于这类情况，就可以通过 @Results 和 @Result 注解来进行绑定，具体如下：

```java
@Results({
    @Result(property = "name", column = "name"),
    @Result(property = "age", column = "age")
})
@Select("SELECT name, age FROM user")
List<User> findAll();
```

在上面代码中，@Result 中的 property 属性对应 User 对象中的成员名，column 对应 SELECT 出的字段名。在该配置中故意没有查出 id 属性，只对 User 对应中的 name 和 age 对象做了映射配置，这样可以通过下面的单元测试来验证查出的 id 为 null，而其他属性不为 null：

```java
@Test
@Rollback
public void testUserMapper() throws Exception {
	List<User> userList = userMapper.findAll();
	for(User user : userList) {
		Assert.assertEquals(null, user.getId());
		Assert.assertNotEquals(null, user.getName());
	}
}
```

### 数据类型

无论是 MyBatis 在预处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出一个值时，都会用类型处理器将获取的值以合适的方式转换成 Java 类型。从 3.4.5 开始，MyBatis 默认支持 JSR-310(日期和时间 API)。

```
 JDBCType            JavaType
  CHAR                String
  VARCHAR             String
  LONGVARCHAR         String
  NUMERIC             java.math.BigDecimal
  DECIMAL             java.math.BigDecimal
  BIT                 boolean
  BOOLEAN             boolean
  TINYINT             byte
  SMALLINT            short
  INTEGER             int
  BIGINT              long
  REAL                float
  FLOAT               double
  DOUBLE              double
  BINARY              byte[]
  VARBINARY           byte[]
  LONGVARBINARY               byte[]
  DATE                java.sql.Date
  TIME                java.sql.Time
  TIMESTAMP           java.sql.Timestamp
  CLOB                Clob
  BLOB                Blob
  ARRAY               Array
  DISTINCT            mapping of underlying type
  STRUCT              Struct
  REF                 Ref
  DATALINK            java.net.URL
```

## 使用 XML 配置

第一步：在应用主类中增加 mapper 的扫描包配置：

```java
@MapperScan("com.didispace.chapter36.mapper")
@SpringBootApplication
public class Chapter36Application {

	public static void main(String[] args) {
		SpringApplication.run(Chapter36Application.class, args);
	}

}
```

第二步：在第一步中指定的 Mapper 包下创建 User 表的 Mapper 定义：

```java
public interface UserMapper {

    User findByName(@Param("name") String name);

    int insert(@Param("name") String name, @Param("age") Integer age);

}
```

第三步：在配置文件中通过 mybatis.mapper-locations 参数指定 xml 配置的位置：

```java
mybatis.mapper-locations=classpath:mapper/*.xml
```

第四步：在第三步中指定的 xml 配置目录下创建 User 表的 mapper 配置：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.didispace.chapter36.mapper.UserMapper">
    <select id="findByName" resultType="com.didispace.chapter36.entity.User">
        SELECT * FROM USER WHERE NAME = #{name}
    </select>

    <insert id="insert">
        INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})
    </insert>
</mapper>
```

到这里从注解方式的 MyBatis 使用方式就改为了 XML 的配置方式了，为了验证是否运行正常，可以通过下面的单元测试来尝试对数据库的写和读操作：

```java
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class Chapter36ApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Rollback
    public void test() throws Exception {
        userMapper.insert("AAA", 20);
        User u = userMapper.findByName("AAA");
        Assert.assertEquals(20, u.getAge().intValue());
    }

}
```

# Links

- https://mp.weixin.qq.com/s/X4pCR662mmFrcRM0tglaLg?from=groupmessage&isappinstalled=0

- https://zhuanlan.zhihu.com/p/88607398

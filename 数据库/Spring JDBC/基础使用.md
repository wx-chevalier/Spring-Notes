# Spring JDBC

Spring JDBC 框架提供了多种访问数据库的方法，其中最著名的就是使用`JdbcTemplate`这个类。这也是主要的用于管理数据库连接与异常处理的类。要使用 Spring JDBC 的话，首先需要在 pom.xml 文件中配置依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.26</version>
</dependency>
```

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

Spring 的 JdbcTemplate 是自动配置的，你可以直接使用@Autowired 或构造函数（推荐）来注入到你自己的 bean 中来使用。先创建 User 表，包含属性 name、age。可以通过执行下面的建表语句：

```sql
CREATE TABLE `User` (
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `age` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci

```

根据数据库中创建的 User 表，创建领域对象：

```java
@Data
@NoArgsConstructor
public class User {
    private String name;
    private Integer age;
}
```

然后定义包含有插入、删除、查询的抽象接口 UserService：

```java
public interface UserService {

    /**
     * 新增一个用户
     *
     * @param name
     * @param age
     */
    int create(String name, Integer age);

    /**
     * 根据name查询用户
     *
     * @param name
     * @return
     */
    List<User> getByName(String name);

    /**
     * 根据name删除用户
     *
     * @param name
     */
    int deleteByName(String name);

    /**
     * 获取用户总量
     */
    int getAllUsers();

    /**
     * 删除所有用户
     */
    int deleteAllUsers();

}
```

最后通过 JdbcTemplate 实现 UserService 中定义的数据访问操作：

```java
@Service
public class UserServiceImpl implements UserService {

    private JdbcTemplate jdbcTemplate;

    UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(String name, Integer age) {
        return jdbcTemplate.update("insert into USER(NAME, AGE) values(?, ?)", name, age);
    }

    @Override
    public List<User> getByName(String name) {
        List<User> users = jdbcTemplate.query("select NAME, AGE from USER where NAME = ?", (resultSet, i) -> {
            User user = new User();
            user.setName(resultSet.getString("NAME"));
            user.setAge(resultSet.getInt("AGE"));
            return user;
        }, name);
        return users;
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("delete from USER where NAME = ?", name);
    }

    @Override
    public int getAllUsers() {
        return jdbcTemplate.queryForObject("select count(1) from USER", Integer.class);
    }

    @Override
    public int deleteAllUsers() {
        return jdbcTemplate.update("delete from USER");
    }

}
```

## 单元测试

创建对 UserService 的单元测试用例，通过创建、删除和查询来验证数据库操作的正确性。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class Chapter31ApplicationTests {

    @Autowired
    private UserService userSerivce;

    @Before
    public void setUp() {
        // 准备，清空user表
        userSerivce.deleteAllUsers();
    }

    @Test
    public void test() throws Exception {
        // 插入5个用户
        userSerivce.create("Tom", 10);
        userSerivce.create("Mike", 11);
        userSerivce.create("Didispace", 30);
        userSerivce.create("Oscar", 21);
        userSerivce.create("Linda", 17);

        // 查询名为Oscar的用户，判断年龄是否匹配
        List<User> userList = userSerivce.getByName("Oscar");
        Assert.assertEquals(21, userList.get(0).getAge().intValue());

        // 查数据库，应该有5个用户
        Assert.assertEquals(5, userSerivce.getAllUsers());

        // 删除两个用户
        userSerivce.deleteByName("Tom");
        userSerivce.deleteByName("Mike");

        // 查数据库，应该有5个用户
        Assert.assertEquals(3, userSerivce.getAllUsers());

    }

}
```

# 数据查询

```java
@SuppressWarnings({ "unchecked", "rawtypes" })
public Employee findById(int id){
    String sql = "SELECT * FROM EMPLOYEE WHERE ID = ?";
    jdbcTemplate = new JdbcTemplate(dataSource);
    Employee employee = (Employee) jdbcTemplate.queryForObject(
    sql, new Object[] { id }, new BeanPropertyRowMapper(Employee.class));
    return employee;
}
```

在 query 中，最后需要传入一个继承自 RowMapper 的实现类，有时候方便起见，也可以直接传入一个 Entity。如果是采用的 RowMapper 模式，需要实现如下的映射器类：

```java
@SuppressWarnings("rawtypes")
public class EmployeeRowMapper implements RowMapper	{
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    Employee employee = new Employee();
    employee.setId(rs.getInt("ID"));
    employee.setName(rs.getString("NAME"));
    employee.setAge(rs.getInt("AGE"));
    return employee;
    }
}
```

最后在调用的时候，把映射器作为最后一个参数传入：

```java
Employee employee = (Employee) jdbcTemplate.queryForObject(sql, new Object[] { id }, new EmployeeRowMapper());
```

# 插入与修改

## Insert

```java
public void insert(Employee employee) {

    String sql = "INSERT INTO EMPLOYEE " +
        "(ID, NAME, AGE) VALUES (?, ?, ?)";

    jdbcTemplate = new JdbcTemplate(dataSource);

    jdbcTemplate.update(sql, new Object[] {
        employee.getId(),
            employee.getName(), employee.getAge()
    });
}
```

有时候需要在插入之后，将插入行自动生成的主键返回，可以使用 jdbcTemplate 中提供的 KeyHolder 来实现：

```java
public class ExampleDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public long addNew(final String name) {
    final PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO `names` (`name`) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        return ps;
      }
    };

    // The newly generated key will be saved in this object
    final KeyHolder holder = new GeneratedKeyHolder();

    jdbcTemplate.update(psc, holder);

    final long newNameId = holder.getKey().longValue();
    return newNameId;
  }
}
```

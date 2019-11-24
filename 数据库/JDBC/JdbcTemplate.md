﻿# Spring JDBC

Spring JDBC 框架提供了多种访问数据库的方法，其中最著名的就是使用`JdbcTemplate`这个类。这也是主要的用于管理数据库连接与异常处理的类。要使用 Spring JDBC 的话，首先需要在 pom.xml 文件中配置依赖项：

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

# 数据查询

```java
@SuppressWarnings({ "unchecked", "rawtypes" })
public Employee findById(int id){
    String sql = "SELECT * FROM EMPLOYEE WHERE ID = ?";
    jdbcTemplate = new JdbcTemplate(dataSource);
    Employee employee = (Employee) jdbcTemplate.queryForObject(
    sql, new Object[] { id }, new BeanPropertyRowMapper(Employee.class));
    return employee;
}
```

在 query 中，最后需要传入一个继承自 RowMapper 的实现类，有时候方便起见，也可以直接传入一个 Entity。如果是采用的 RowMapper 模式，需要实现如下的映射器类：

```java
@SuppressWarnings("rawtypes")
public class EmployeeRowMapper implements RowMapper	{
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    Employee employee = new Employee();
    employee.setId(rs.getInt("ID"));
    employee.setName(rs.getString("NAME"));
    employee.setAge(rs.getInt("AGE"));
    return employee;
    }
}
```

最后在调用的时候，把映射器作为最后一个参数传入：

```java
Employee employee = (Employee) jdbcTemplate.queryForObject(sql, new Object[] { id }, new EmployeeRowMapper());
```

## Insert

```java
public void insert(Employee employee){

String sql = "INSERT INTO EMPLOYEE " +
"(ID, NAME, AGE) VALUES (?, ?, ?)";

jdbcTemplate = new JdbcTemplate(dataSource);

jdbcTemplate.update(sql, new Object[] { employee.getId(),
employee.getName(), employee.getAge()
});
}
```

有时候需要在插入之后，将插入行自动生成的主键返回，可以使用 jdbcTemplate 中提供的 KeyHolder 来实现：

```java
public class ExampleDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public long addNew(final String name) {
    final PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO `names` (`name`) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        return ps;
      }
    };

    // The newly generated key will be saved in this object
    final KeyHolder holder = new GeneratedKeyHolder();

    jdbcTemplate.update(psc, holder);

    final long newNameId = holder.getKey().longValue();
    return newNameId;
  }
}
```

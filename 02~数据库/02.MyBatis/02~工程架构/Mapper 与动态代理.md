# Mapper 与动态代理

所谓动态代理，通过拦截器方法回调，对目标 target 方法进行增强。

# 自定义 JDK 动态代理实现 Mapper

首先定义一个 POJO。

```js
public class User {
 private Integer id;
 private String name;
 private int age;

 public User(Integer id, String name, int age) {
  this.id = id;
  this.name = name;
  this.age = age;
 }
 // getter setter
}
```

再定义一个接口 UserMapper.java。

```java
public interface UserMapper {
 public User getUserById(Integer id);
}
```

自定义一个 InvocationHandler。

```java

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MapperProxy implements InvocationHandler {

 @SuppressWarnings("unchecked")
 public <T> T newInstance(Class<T> clz) {
  return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[] { clz }, this);
 }

 @Override
 public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  if (Object.class.equals(method.getDeclaringClass())) {
   try {
    // 诸如hashCode()、toString()、equals()等方法，将target指向当前对象this
    return method.invoke(this, args);
   } catch (Throwable t) {
   }
  }
  // 投鞭断流
  return new User((Integer) args[0], "zhangsan", 18);
 }
}
```

上面代码中的 target，在执行 Object.java 内的方法时，target 被指向了 this。

```java
public static void main(String[] args) {
 MapperProxy proxy = new MapperProxy();

 UserMapper mapper = proxy.newInstance(UserMapper.class);
 User user = mapper.getUserById(1001);

 System.out.println("ID:" + user.getId());
 System.out.println("Name:" + user.getName());
 System.out.println("Age:" + user.getAge());

 System.out.println(mapper.toString());
}
```

# Mybatis 自动映射器 Mapper

```java
public static void main(String[] args) {
    SqlSession sqlSession = MybatisSqlSessionFactory.openSession();
    try {
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<Student> students = studentMapper.findAllStudents();

        for (Student student : students) {
            System.out.println(student);
        }
    } finally {
        sqlSession.close();
    }
}

public interface StudentMapper {
    List<Student> findAllStudents();
    Student findStudentById(Integer id);
    void insertStudent(Student student);
}
```

org.apache.ibatis.binding.MapperProxy.java 部分源码。

```java
public class MapperProxy<T> implements InvocationHandler, Serializable {

  private static final long serialVersionUID = -6424540398559729838L;
  private final SqlSession sqlSession;
  private final Class<T> mapperInterface;
  private final Map<Method, MapperMethod> methodCache;

  public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (Object.class.equals(method.getDeclaringClass())) {
      try {
        return method.invoke(this, args);
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }

    final MapperMethod mapperMethod = cachedMapperMethod(method);
    return mapperMethod.execute(sqlSession, args);
  }
  // ...
```

org.apache.ibatis.binding.MapperProxyFactory.java 部分源码。

```java
public class MapperProxyFactory<T> {

  private final Class<T> mapperInterface;

  @SuppressWarnings("unchecked")
  protected T newInstance(MapperProxy<T> mapperProxy) {
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }
```

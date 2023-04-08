# Cache 注解

User 实体的数据访问实现：

```java
public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);

    User findByNameAndAge(String name, Integer age);

    @Query("from User u where u.name=:name")
    User findUser(@Param("name") String name);

}
```

在 pom.xml 中引入 cache 依赖，添加如下内容：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

在 Spring Boot 主类中增加@EnableCaching 注解开启缓存功能，如下：

```java
@EnableCaching
@SpringBootApplication
public class Chapter51Application {

	public static void main(String[] args) {
		SpringApplication.run(Chapter51Application.class, args);
	}

}
```

在数据访问接口中，增加缓存配置注解，如：

```java
@CacheConfig(cacheNames = "users")
public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable
    User findByName(String name);

}
```

到这里，我们可以看到，在调用第二次 findByName 函数时，没有再执行 select 语句，也就直接减少了一次数据库的读取操作。

回过头来我们再来看这里使用到的两个注解分别作了什么事情：

- `@CacheConfig`：主要用于配置该类中会用到的一些共用的缓存配置。在这里`@CacheConfig(cacheNames = "users")`：配置了该数据访问对象中返回的内容将存储于名为 users 的缓存对象中，我们也可以不使用该注解，直接通过`@Cacheable`自己配置缓存集的名字来定义。

- @Cacheable：配置了 findByName 函数的返回值将被加入缓存。同时在查询时，会先从缓存中获取，若不存在才再发起对数据库的访问。该注解主要有下面几个参数：

  - `value`、`cacheNames`：两个等同的参数（`cacheNames`为 Spring 4 新增，作为`value`的别名），用于指定缓存存储的集合名。由于 Spring 4 中新增了`@CacheConfig`，因此在 Spring 3 中原本必须有的`value`属性，也成为非必需项了
  - `key`：缓存对象存储在 Map 集合中的 key 值，非必需，缺省按照函数的所有参数组合作为 key 值，若自己配置需使用 SpEL 表达式，比如：`@Cacheable(key = "#p0")`：使用函数第一个参数作为缓存的 key 值，更多关于 SpEL 表达式的详细内容可参考[官方文档](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html#cache-spel-context)
  - `condition`：缓存对象的条件，非必需，也需使用 SpEL 表达式，只有满足表达式条件的内容才会被缓存，比如：`@Cacheable(key = "#p0", condition = "#p0.length() < 3")`，表示只有当第一个参数的长度小于 3 的时候才会被缓存，若做此配置上面的 AAA 用户就不会被缓存，读者可自行实验尝试。
  - `unless`：另外一个缓存条件参数，非必需，需使用 SpEL 表达式。它不同于`condition`参数的地方在于它的判断时机，该条件是在函数被调用之后才做判断的，所以它可以通过对 result 进行判断。
  - `keyGenerator`：用于指定 key 生成器，非必需。若需要指定一个自定义的 key 生成器，我们需要去实现`org.springframework.cache.interceptor.KeyGenerator`接口，并使用该参数来指定。需要注意的是：**该参数与`key`是互斥的**
  - `cacheManager`：用于指定使用哪个缓存管理器，非必需。只有当有多个时才需要使用
  - `cacheResolver`：用于指定使用那个缓存解析器，非必需。需通过`org.springframework.cache.interceptor.CacheResolver`接口来实现自己的缓存解析器，并用该参数指定。

除了这里用到的两个注解之外，还有下面几个核心注解：

- `@CachePut`：配置于函数上，能够根据参数定义条件来进行缓存，它与`@Cacheable`不同的是，它每次都会真是调用函数，所以主要用于数据新增和修改操作上。它的参数与`@Cacheable`类似，具体功能可参考上面对`@Cacheable`参数的解析
- @CacheEvict：配置于函数上，通常用在删除方法上，用来从缓存中移除相应数据。除了同@Cacheable 一样的参数之外，它还有下面两个参数：
  - `allEntries`：非必需，默认为 false。当为 true 时，会移除所有数据
  - `beforeInvocation`：非必需，默认为 false，会在调用方法之后移除数据。当为 true 时，会在调用方法之前移除数据。

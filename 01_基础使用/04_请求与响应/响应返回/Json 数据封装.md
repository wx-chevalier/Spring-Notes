# Json 数据封装

在项目开发中，接口与接口之间，前后端之间数据的传输都使用 Json 格式，在 Spring Boot 中，接口返回 Json 格式的数据很简单，在 Controller 中使用@RestController 注解即可返回 Json 格式的数据，@RestController 也是 Spring Boot 新增的一个注解，我们点进去看一下该注解都包含了哪些东西。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {
    String value() default "";
}
```

可以看出，@RestController 注解包含了原来的 @Controller 和 @ResponseBody 注解，使用过 Spring 的朋友对 @Controller 注解已经非常了解了，这里不再赘述，@ResponseBody 注解是将返回的数据结构转换为 Json 格式。所以在默认情况下，使用了 @RestController 注解即可将返回的数据结构转换成 Json 格式，Spring Boot 中默认使用的 Json 解析技术框架是 jackson。我们点开 pom.xml 中的 spring-boot-starter-web 依赖，可以看到一个 spring-boot-starter-json 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-json</artifactId>
    <version>2.0.3.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

Spring Boot 中对依赖都做了很好的封装，可以看到很多 spring-boot-starter-xxx 系列的依赖，这是 Spring Boot 的特点之一，不需要人为去引入很多相关的依赖了，starter-xxx 系列直接都包含了所必要的依赖，所以我们再次点进去上面这个 spring-boot-starter-json 依赖，可以看到：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.6</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jdk8</artifactId>
    <version>2.9.6</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.9.6</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-parameter-names</artifactId>
    <version>2.9.6</version>
    <scope>compile</scope>
</dependency>
```

到此为止，我们知道了 Spring Boot 中默认使用的 json 解析框架是 jackson。下面我们看一下默认的 jackson 框架对常用数据类型的转 Json 处理。

# 基于 Jackson 的默认 Json 处理

在实际项目中，常用的数据结构无非有类对象、List 对象、Map 对象，我们看一下默认的 jackson 框架对这三个常用的数据结构转成 json 后的格式如何。

> 参阅《[Java-Notes](https://github.com/wx-chevalier/Java-Notes?q=)》了解更多有关 Jackson 的使用。

为了测试，我们需要创建一个实体类，这里我们就用 User 来演示。

```java

public class User {
    private Long id;
    private String username;
    private String password;
	/* 省略get、set和带参构造方法 */
}
```

然后我们创建一个 Controller，分别返回 User 对象、List<User> 和 `Map<String, Object>`。

```java
@RestController
@RequestMapping("/json")
public class JsonController {
    @RequestMapping("/user")
    public User getUser() {
        return new User(1, "test", "123456");
    }
    @RequestMapping("/list")
    public List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        User user1 = new User(1, "test", "123456");
        User user2 = new User(2, "testtest", "123456");
        userList.add(user1);
        userList.add(user2);
        return userList;
    }
    @RequestMapping("/map")
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>(3);
        User user = new User(1, "test", "123456");
        map.put("info", user);
        map.put("cnt", 4153);
        return map;
    }
}
```

接口，分别返回了一个 User 对象、一个 List 集合和一个 Map 集合，其中 Map 集合中的 value 存的是不同的数据类型。接下来我们依次来测试一下效果。

```sh
# 在浏览器中输入：localhost:8080/json/user 返回 json 如下：

{"id":1,"username":"test","password":"123456"}

# 在浏览器中输入：localhost:8080/json/list 返回 json 如下：

[{"id":1,"username":"test","password":"123456"},{"id":2,"username":"testtest","password":"123456"}]

# 在浏览器中输入：localhost:8080/json/map 返回 json 如下：

{"info":{"id":1,"username":"test","password":"123456"},"cnt":4153}

```

## jackson 中对 null 的处理

在实际项目中，我们难免会遇到一些 null 值出现，我们转 json 时，是不希望有这些 null 出现的，比如我们期望所有的 null 在转 json 时都变成 "" 这种空字符串，那怎么做呢？在 Spring Boot 中，我们做一下配置即可，新建一个 jackson 的配置类：

```java
@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString("");
            }
        });
        return objectMapper;
    }
}
```

该段及会将 null 转化为空字符串。

# fastJson

使用 fastJson 需要导入依赖，本课程使用 1.2.35 版本，依赖如下：

```xml
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.35</version>
</dependency>
```

使用 fastJson 时，对 null 的处理和 jackson 有些不同，需要继承 WebMvcConfigurationSupport 类，然后覆盖 configureMessageConverters 方法，在方法中，我们可以选择对要实现 null 转换的场景，配置好即可。如下：

```java
@Configuration
public class FastJsonConfig extends WebMvcConfigurationSupport {
    /**
     * 使用阿里 FastJson 作为JSON MessageConverter
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
                // 保留map空的字段
                SerializerFeature.WriteMapNullValue,
                // 将String类型的null转成""
                SerializerFeature.WriteNullStringAsEmpty,
                // 将Number类型的null转成0
                SerializerFeature.WriteNullNumberAsZero,
                // 将List类型的null转成[]
                SerializerFeature.WriteNullListAsEmpty,
                // 将Boolean类型的null转成false
                SerializerFeature.WriteNullBooleanAsFalse,
                // 避免循环引用
                SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        List<MediaType> mediaTypeList = new ArrayList<>();
        // 解决中文乱码问题，相当于在Controller上的@RequestMapping中加了个属性produces = "application/json"
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypeList);
        converters.add(converter);
    }
}
```

# 封装统一返回的数据结构

以上是 Spring Boot 返回 json 的几个代表的例子，但是在实际项目中，除了要封装数据之外，我们往往需要在返回的 json 中添加一些其他信息，比如返回一些状态码 code ，返回一些 msg 给调用者，这样调用者可以根据 code 或者 msg 做一些逻辑判断。所以在实际项目中，我们需要封装一个统一的 json 返回结构存储返回信息。由于封装的 json 数据的类型不确定，所以在定义统一的 json 结构时，我们需要用到泛型。统一的 json 结构中属性包括数据、状态码、提示信息即可，构造方法可以根据实际业务需求做相应的添加即可，一般来说，应该有默认的返回结构，也应该有用户指定的返回结构。如下：

```java
public class JsonResult<T> {
    private T data;
    private String code;
    private String msg;
    /**
     * 若没有数据返回，默认状态码为0，提示信息为：操作成功！
     */
    public JsonResult() {
        this.code = "0";
        this.msg = "操作成功！";
    }
    /**
     * 若没有数据返回，可以人为指定状态码和提示信息
     * @param code
     * @param msg
     */
    public JsonResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    /**
     * 有数据返回时，状态码为0，默认提示信息为：操作成功！
     * @param data
     */
    public JsonResult(T data) {
        this.data = data;
        this.code = "0";
        this.msg = "操作成功！";
    }
    /**
     * 有数据返回，状态码为0，人为指定提示信息
     * @param data
     * @param msg
     */
    public JsonResult(T data, String msg) {
        this.data = data;
        this.code = "0";
        this.msg = msg;
    }
    // 省略get和set方法
}
```

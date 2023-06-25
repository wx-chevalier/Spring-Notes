# 请求与响应

# 完整示例

在构建 Spring MVC 项目时，我们会依赖于以下注解：

- @Controller：修饰 class，用来创建处理 http 请求的对象
- @RestController：Spring4 之后加入的注解，原来在 @Controller 中返回 json 需要 @ResponseBody 来配合，如果直接用 @RestController 替代 @Controller 就不需要再配置 @ResponseBody，默认返回 json 格式
- @RequestMapping：配置 url 映射。现在更多的也会直接用以 Http Method 直接关联的映射注解来定义，比如：GetMapping、PostMapping、DeleteMapping、PutMapping 等

下面我们通过使用 Spring MVC 来实现一组对 User 对象操作的 RESTful API，配合注释详细说明在 Spring MVC 中如何映射 HTTP 请求、如何传参、如何编写单元测试。

```java
@Data
public class User {

    private Long id;
    private String name;
    private Integer age;

}
```

然后实现对 User 对象的操作接口：

```java
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestController
@RequestMapping(value = "/users")     // 通过这里配置使下面的映射都在/users下
public class UserController {

    // 创建线程安全的Map，模拟users信息的存储
    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<Long, User>());

    /**
     * 处理"/users/"的GET请求，用来获取用户列表
     *
     * @return
     */
    @GetMapping("/")
    public List<User> getUserList() {
        // 还可以通过@RequestParam从页面中传递参数来进行查询条件或者翻页信息的传递
        List<User> r = new ArrayList<User>(users.values());
        return r;
    }

    /**
     * 处理"/users/"的POST请求，用来创建User
     *
     * @param user
     * @return
     */
    @PostMapping("/")
    public String postUser(@RequestBody User user) {
        // @RequestBody注解用来绑定通过http请求中application/json类型上传的数据
        users.put(user.getId(), user);
        return "success";
    }

    /**
     * 处理"/users/{id}"的GET请求，用来获取url中id值的User信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // url中的id可通过@PathVariable绑定到函数的参数中
        return users.get(id);
    }

    /**
     * 处理"/users/{id}"的PUT请求，用来更新User信息
     *
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/{id}")
    public String putUser(@PathVariable Long id, @RequestBody User user) {
        User u = users.get(id);
        u.setName(user.getName());
        u.setAge(user.getAge());
        users.put(id, u);
        return "success";
    }

    /**
     * 处理"/users/{id}"的DELETE请求，用来删除User
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        users.remove(id);
        return "success";
    }

}
```

下面针对该 Controller 编写测试用例验证正确性，具体如下。当然也可以通过浏览器插件等进行请求提交验证。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class Chapter21ApplicationTests {

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    public void testUserController() throws Exception {
        // 测试UserController
        RequestBuilder request;

        // 1、get查一下user列表，应该为空
        request = get("/users/");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

        // 2、post提交一个user
        request = post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"测试大师\",\"age\":20}");
        mvc.perform(request)
                .andExpect(content().string(equalTo("success")));

        // 3、get获取user列表，应该有刚才插入的数据
        request = get("/users/");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"id\":1,\"name\":\"测试大师\",\"age\":20}]")));

        // 4、put修改id为1的user
        request = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"测试终极大师\",\"age\":30}");
        mvc.perform(request)
                .andExpect(content().string(equalTo("success")));

        // 5、get一个id为1的user
        request = get("/users/1");
        mvc.perform(request)
                .andExpect(content().string(equalTo("{\"id\":1,\"name\":\"测试终极大师\",\"age\":30}")));

        // 6、del删除id为1的user
        request = delete("/users/1");
        mvc.perform(request)
                .andExpect(content().string(equalTo("success")));

        // 7、get查一下user列表，应该为空
        request = get("/users/");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

    }

}

```

# 常用注解

## @RestController

@RestController 是 Spring Boot 新增的一个注解，我们看一下该注解都包含了哪些东西。

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

可以看出，@RestController 注解包含了原来的 @Controller 和 @ResponseBody 注解，使用过 Spring 的朋友对 @Controller 注解已经非常了解了，这里不再赘述，@ResponseBody 注解是将返回的数据结构转换为 Json 格式。所以 @RestController 可以看作是 @Controller 和 @ResponseBody 的结合体，相当于偷个懒，我们使用 @RestController 之后就不用再使用 @Controller 了。但是需要注意一个问题：如果是前后端分离，不用模板渲染的话，比如 Thymeleaf，这种情况下是可以直接使用@RestController 将数据以 json 格式传给前端，前端拿到之后解析；但如果不是前后端分离，需要使用模板来渲染的话，一般 Controller 中都会返回到具体的页面，那么此时就不能使用@RestController 了，比如：

```java
public String getUser() {
	return "user";
}
```

其实是需要返回到 user.html 页面的，如果使用 @RestController 的话，会将 user 作为字符串返回的，所以这时候我们需要使用 @Controller 注解。

## @RequestMapping

@RequestMapping 是一个用来处理请求地址映射的注解，它可以用于类上，也可以用于方法上。在类的级别上的注解会将一个特定请求或者请求模式映射到一个控制器之上，表示类中的所有响应请求的方法都是以该地址作为父路径；在方法的级别表示进一步指定到处理方法的映射关系。该注解有 6 个属性，一般在项目中比较常用的有三个属性：value、method 和 produces。

- value 属性：指定请求的实际地址，value 可以省略不写
- method 属性：指定请求的类型，主要有 GET、PUT、POST、DELETE，默认为 GET
- produces 属性：指定返回内容类型，如 produces = "application/json; charset=UTF-8"

@RequestMapping 注解比较简单，举个例子：

```java
@RestController
@RequestMapping(value = "/test", produces = "application/json; charset=UTF-8")
public class TestController {
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String testGet() {
        return "success";
    }
}
```

这个很简单，启动项目在浏览器中输入 localhost:8080/test/get 测试一下即可。针对四种不同的请求方式，是有相应注解的，不用每次在 @RequestMapping 注解中加 method 属性来指定，上面的 GET 方式请求可以直接使用 @GetMapping("/get") 注解，效果一样。相应地，PUT 方式、POST 方式和 DELETE 方式对应的注解分别为 @PutMapping、@PostMapping 和 DeleteMapping。

## @PathVariable

@PathVariable 注解主要是用来获取 url 参数，Spring Boot 支持 restfull 风格的 url，比如一个 GET 请求携带一个参数 id 过来，我们将 id 作为参数接收，可以使用 @PathVariable 注解。如下：

```java

@GetMapping("/user/{id}")
public String testPathVariable(@PathVariable Integer id) {
	System.out.println("获取到的id为：" + id);
	return "success";
}
```

这里需要注意一个问题，如果想要 url 中占位符中的 id 值直接赋值到参数 id 中，需要保证 url 中的参数和方法接收参数一致，否则就无法接收。如果不一致的话，其实也可以解决，需要用 @PathVariable 中的 value 属性来指定对应关系。如下：

```java

@RequestMapping("/user/{idd}")
public String testPathVariable(@PathVariable(value = "idd") Integer id) {
	System.out.println("获取到的id为：" + id);
	return "success";
}
```

对于访问的 url，占位符的位置可以在任何位置，不一定非要在最后，比如这样也行：/xxx/{id}/user。另外，url 也支持多个占位符，方法参数使用同样数量的参数来接收，原理和一个参数是一样的，例如：

```java

@GetMapping("/user/{idd}/{name}")
    public String testPathVariable(@PathVariable(value = "idd") Integer id, @PathVariable String name) {
        System.out.println("获取到的id为：" + id);
        System.out.println("获取到的name为：" + name);
        return "success";
    }
```

运行项目，在浏览器中请求 localhost:8080/test/user/2/zhangsan 可以看到控制台输出如下信息：

```java
获取到的id为：2
获取到的name为：zhangsan
```

所以支持多个参数的接收。同样地，如果 url 中的参数和方法中的参数名称不同的话，也需要使用 value 属性来绑定两个参数。

## @RequestParam

@RequestParam 注解顾名思义，也是获取请求参数的，上面我们介绍了 @PathValiable 注解也是获取请求参数的，那么 @RequestParam 和 @PathVariable 有什么不同呢？主要区别在于：@PathValiable 是从 url 模板中获取参数值，即这种风格的 url：http://localhost:8080/user/{id} ；而 @RequestParam 是从 request 里面获取参数值，即这种风格的 url：http://localhost:8080/user?id=1 。我们使用该 url 带上参数 id 来测试一下如下代码：

```java
@GetMapping("/user")
public String testRequestParam(@RequestParam Integer id) {
	System.out.println("获取到的id为：" + id);
	return "success";
}
```

可以正常从控制台打印出 id 信息。同样地，url 上面的参数和方法的参数需要一致，如果不一致，也需要使用 value 属性来说明，比如 url 为：http://localhost:8080/user?idd=1：

```java

@RequestMapping("/user")
public String testRequestParam(@RequestParam(value = "idd", required = false) Integer id) {
	System.out.println("获取到的id为：" + id);
	return "success";
}
```

除了 value 属性外，还有个两个属性比较常用：

- required 属性：true 表示该参数必须要传，否则就会报 404 错误，false 表示可有可无。
- defaultValue 属性：默认值，表示如果请求中没有同名参数时的默认值。

从 url 中可以看出，@RequestParam 注解用于 GET 请求上时，接收拼接在 url 中的参数。除此之外，该注解还可以用于 POST 请求，接收前端表单提交的参数，假如前端通过表单提交 username 和 password 两个参数，那我们可以使用 @RequestParam 来接收，用法和上面一样。

```java
@PostMapping("/form1")
public String testForm(@RequestParam String username, @RequestParam String password) {
    System.out.println("获取到的username为：" + username);
    System.out.println("获取到的password为：" + password);
    return "success";
}
```

如果表单数据很多，我们不可能在后台方法中写上很多参数，每个参数还要 @RequestParam 注解。针对这种情况，我们需要封装一个实体类来接收这些参数，实体中的属性名和表单中的参数名一致即可。

```java
public class User {
	private String username;
	private String password;
	// set get
}
```

使用实体接收的话，我们不能在前面加 @RequestParam 注解了，直接使用即可。

```java

@PostMapping("/form2")
public String testForm(User user) {
    System.out.println("获取到的username为：" + user.getUsername());
    System.out.println("获取到的password为：" + user.getPassword());
    return "success";
}
```

## @RequestBody

@RequestBody 注解用于接收前端传来的实体，接收参数也是对应的实体，比如前端通过 json 提交传来两个参数 username 和 password，此时我们需要在后端封装一个实体来接收。在传递的参数比较多的情况下，使用 @RequestBody 接收会非常方便。例如：

```java
public class User {
	private String username;
	private String password;
	// set get
}

@PostMapping("/user")
public String testRequestBody(@RequestBody User user) {
	System.out.println("获取到的username为：" + user.getUsername());
	System.out.println("获取到的password为：" + user.getPassword());
	return "success";
}
```

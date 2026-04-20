# Swagger

随着前后端分离架构和微服务架构的流行，我们使用 Spring Boot 来构建 RESTful API 项目的场景越来越多。通常我们的一个 RESTful API 就有可能要服务于多个不同的开发人员或开发团队：IOS 开发、Android 开发、Web 开发甚至其他的后端服务等。为了减少与其他团队平时开发期间的频繁沟通成本，传统做法就是创建一份 RESTful API 文档来记录所有接口细节，然而这样的做法有以下几个问题：

- 由于接口众多，并且细节复杂（需要考虑不同的 HTTP 请求类型、HTTP 头部信息、HTTP 请求内容等），高质量地创建这份文档本身就是件非常吃力的事，下游的抱怨声不绝于耳。
- 随着时间推移，不断修改接口实现的时候都必须同步修改接口文档，而文档与代码又处于两个不同的媒介，除非有严格的管理机制，不然很容易导致不一致现象。

为了解决上面这样的问题，本文将介绍 RESTful API 的重磅好伙伴 Swagger2，它可以轻松的整合到 Spring Boot 中，并与 Spring MVC 程序配合组织出强大 RESTful API 文档。它既可以减少我们创建文档的工作量，同时说明内容又整合入实现代码中，让维护文档和修改代码整合为一体，可以让我们在修改代码逻辑的同时方便的修改文档说明。另外 Swagger2 也提供了强大的页面测试功能来调试每个 RESTful API。

首先添加 swagger-spring-boot-starter 依赖，在 pom.xml 中加入依赖，具体如下：

```xml
<dependency>
    <groupId>com.spring4all</groupId>
    <artifactId>swagger-spring-boot-starter</artifactId>
    <version>1.9.0.RELEASE</version>
</dependency>
```

应用主类中添加 @EnableSwagger2Doc 注解，具体如下：

```java
@EnableSwagger2Doc
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

application.properties 中配置文档相关内容，比如：

```yml
swagger.title=spring-boot-starter-swagger
swagger.description=Starter for swagger 2.x
swagger.version=1.4.0.RELEASE
swagger.license=Apache License, Version 2.0
swagger.licenseUrl=https://www.apache.org/licenses/LICENSE-2.0.html
swagger.termsOfServiceUrl=https://github.com/dyc87112/spring-boot-starter-swagger
swagger.contact.name=didi
swagger.contact.url=http://blog.didispace.com
swagger.contact.email=dyc87112@qq.com
swagger.base-package=com.didispace
swagger.base-path=/**
```

各参数配置含义如下：

- `swagger.title`：标题
- `swagger.description`：描述
- `swagger.version`：版本
- `swagger.license`：许可证
- `swagger.licenseUrl`：许可证 URL
- `swagger.termsOfServiceUrl`：服务条款 URL
- `swagger.contact.name`：维护人
- `swagger.contact.url`：维护人 URL
- `swagger.contact.email`：维护人 email
- `swagger.base-package`：swagger 扫描的基础包，默认：全扫描
- `swagger.base-path`：需要处理的基础 URL 规则，默认：/\*\*

# 添加文档内容

在整合完 Swagger 之后，在 http://localhost:8080/swagger-ui.html 页面中可以看到，关于各个接口的描述还都是英文或遵循代码定义的名称产生的。这些内容对用户并不友好，所以我们需要自己增加一些说明来丰富文档内容。如下所示，我们通过@Api，@ApiOperation 注解来给 API 增加说明、通过@ApiImplicitParam、@ApiModel、@ApiModelProperty 注解来给参数增加说明。

```java
@Api(tags = "用户管理")
@RestController
@RequestMapping(value = "/users")     // 通过这里配置使下面的映射都在/users下
public class UserController {

    // 创建线程安全的Map，模拟users信息的存储
    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    @GetMapping("/")
    @ApiOperation(value = "获取用户列表")
    public List<User> getUserList() {
        List<User> r = new ArrayList<>(users.values());
        return r;
    }

    @PostMapping("/")
    @ApiOperation(value = "创建用户", notes = "根据User对象创建用户")
    public String postUser(@RequestBody User user) {
        users.put(user.getId(), user);
        return "success";
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息")
    public User getUser(@PathVariable Long id) {
        return users.get(id);
    }

    @PutMapping("/{id}")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "用户编号", required = true, example = "1")
    @ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    public String putUser(@PathVariable Long id, @RequestBody User user) {
        User u = users.get(id);
        u.setName(user.getName());
        u.setAge(user.getAge());
        users.put(id, u);
        return "success";
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "根据url的id来指定删除对象")
    public String deleteUser(@PathVariable Long id) {
        users.remove(id);
        return "success";
    }

}

@Data
@ApiModel(description="用户实体")
public class User {

    @ApiModelProperty("用户编号")
    private Long id;
    @ApiModelProperty("用户姓名")
    private String name;
    @ApiModelProperty("用户年龄")
    private Integer age;

}
```

# 接口分组

我们在 Spring Boot 中定义各个接口是以 Controller 作为第一级维度来进行组织的，Controller 与具体接口之间的关系是一对多的关系。我们可以将同属一个模块的接口定义在一个 Controller 里。默认情况下，Swagger 是以 Controller 为单位，对接口进行分组管理的。这个分组的元素在 Swagger 中称为 Tag，但是这里的 Tag 与接口的关系并不是一对多的，它支持更丰富的多对多关系。

## 默认分组

首先，我们通过一个简单的例子，来看一下默认情况，Swagger 是如何根据 Controller 来组织 Tag 与接口关系的。定义两个 Controller，分别负责教师管理与学生管理接口，比如下面这样：

```java
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {

    @GetMapping("/xxx")
    public String xxx() {
        return "xxx";
    }

}

@RestController
@RequestMapping(value = "/student")
static class StudentController {

    @ApiOperation("获取学生清单")
    @GetMapping("/list")
    public String bbb() {
        return "bbb";
    }

    @ApiOperation("获取教某个学生的老师清单")
    @GetMapping("/his-teachers")
    public String ccc() {
        return "ccc";
    }

    @ApiOperation("创建一个学生")
    @PostMapping("/aaa")
    public String aaa() {
        return "aaa";
    }

}
```

启动应用之后，我们可以看到 Swagger 中这两个 Controller 是这样组织的：

![Swagger Controller 分组](https://s3.ax1x.com/2021/02/07/ytOLWt.png)

图中标出了 Swagger 默认生成的 Tag 与 Spring Boot 中 Controller 展示的内容与位置。

## 自定义默认分组的名称

接着，我们可以再试一下，通过@Api 注解来自定义 Tag，比如这样：

```java
@Api(tags = "教师管理")
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {

    // ...

}

@Api(tags = "学生管理")
@RestController
@RequestMapping(value = "/student")
static class StudentController {

    // ...

}
```

再次启动应用之后，我们就看到了如下的分组内容，代码中@Api 定义的 tags 内容替代了默认产生的 teacher-controller 和 student-controller。

![自定义 Tag](https://s3.ax1x.com/2021/02/07/ytOvy8.png)

## 合并 Controller 分组

到这里，我们还都只是使用了 Tag 与 Controller 一一对应的情况，Swagger 中还支持更灵活的分组。我们可以通过定义同名的 Tag 来汇总 Controller 中的接口，比如我们可以定义一个 Tag 为“教学管理”，让这个分组同时包含教师管理和学生管理的所有接口，可以这样来实现：

```java
@Api(tags = {"教师管理", "教学管理"})
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {

    // ...

}

@Api(tags = {"学生管理", "教学管理"})
@RestController
@RequestMapping(value = "/student")
static class StudentController {

    // ...

}

```

最终效果如下：

![多分组](https://s3.ax1x.com/2021/02/07/ytXPFs.png)

## 更细粒度的接口分组

通过@Api 可以实现将 Controller 中的接口合并到一个 Tag 中，但是如果我们希望精确到某个接口的合并呢？比如这样的需求：“教学管理”包含“教师管理”中所有接口以及“学生管理”管理中的“获取学生清单”接口（不是全部接口）。那么上面的实现方式就无法满足了。这时候发，我们可以通过使用@ApiOperation 注解中的 tags 属性做更细粒度的接口分类定义，比如上面的需求就可以这样子写：

```java
@Api(tags = {"教师管理","教学管理"})
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {

    @ApiOperation(value = "xxx")
    @GetMapping("/xxx")
    public String xxx() {
        return "xxx";
    }

}

@Api(tags = {"学生管理"})
@RestController
@RequestMapping(value = "/student")
static class StudentController {

    @ApiOperation(value = "获取学生清单", tags = "教学管理")
    @GetMapping("/list")
    public String bbb() {
        return "bbb";
    }

    @ApiOperation("获取教某个学生的老师清单")
    @GetMapping("/his-teachers")
    public String ccc() {
        return "ccc";
    }

    @ApiOperation("创建一个学生")
    @PostMapping("/aaa")
    public String aaa() {
        return "aaa";
    }

}
```

效果如下图所示：

![部分分组](https://s3.ax1x.com/2021/02/07/ytXVyT.png)

# 内容的顺序

在完成了接口分组之后，对于接口内容的展现顺序又是众多用户特别关注的点，其中主要涉及三个方面：分组的排序、接口的排序以及参数的排序，下面我们就来逐个说说如何配置与使用。

## 分组的排序

关于分组排序，也就是 Tag 的排序。目前版本的 Swagger 支持并不太好，通过文档我们可以找到关于 Tag 排序的配置方法。Swagger 只提供了一个选项，就是按字母顺序排列。那么我们要如何实现排序呢？这里笔者给一个不需要扩展源码，仅依靠使用方式的定义来实现排序的建议：为 Tag 的命名做编号。比如：

```java
@Api(tags = {"1-教师管理","3-教学管理"})
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {

    // ...

}

@Api(tags = {"2-学生管理"})
@RestController
@RequestMapping(value = "/student")
static class StudentController {

    @ApiOperation(value = "获取学生清单", tags = "3-教学管理")
    @GetMapping("/list")
    public String bbb() {
        return "bbb";
    }

    // ...

}
```

## 接口的排序

在完成了分组排序问题之后，在来看看同一分组内各个接口该如何实现排序。同样的，凡事先查文档，可以看到 Swagger 也提供了相应的配置，下面也分两种配置方式介绍：

```yml
swagger.ui-config.operations-sorter=alpha
```

## 参数的排序

完成了接口的排序之后，更细粒度的就是请求参数的排序了。默认情况下，Swagger 对 Model 参数内容的展现也是按字母顺序排列的。如果我们希望可以按照 Model 中定义的成员变量顺序来展现，那么需要我们通过@ApiModelProperty 注解的 position 参数来实现位置的设置，比如：

```java
@Data
@ApiModel(description = "用户实体")
public class User {

    @ApiModelProperty(value = "用户编号", position = 1)
    private Long id;

    @NotNull
    @Size(min = 2, max = 5)
    @ApiModelProperty(value = "用户姓名", position = 2)
    private String name;

    @NotNull
    @Max(100)
    @Min(10)
    @ApiModelProperty(value = "用户年龄", position = 3)
    private Integer age;

    @NotNull
    @Email
    @ApiModelProperty(value = "用户邮箱", position = 4)
    private String email;

}
```

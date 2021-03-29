# Thymeleaf

Thymeleaf 是适用于 Web 和独立环境的现代服务器端 Java 模板引擎。Thymeleaf 的主要目标是为您的开发工作流程带来优雅的自然模板 - 可以在浏览器中正确显示的 HTML，也可以用作静态原型，从而在开发团队中实现更强大的协作。

# 快速开始

在 Spring Boot 中使用 thymeleaf 模板需要引入依赖，可以在创建项目工程时勾选 Thymeleaf，也可以创建之后再手动导入，如下：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

另外，在 html 页面上如果要使用 thymeleaf 模板，需要在页面标签中引入：

```xml
<html xmlns:th="http://www.thymeleaf.org">
```

因为 Thymeleaf 中已经有默认的配置了，我们不需要再对其做过多的配置，有一个需要注意一下，Thymeleaf 默认是开启页面缓存的，所以在开发的时候，需要关闭这个页面缓存，配置如下。

```yaml
spring:
  thymeleaf:
    cache: false #关闭缓存
```

否则会有缓存，导致页面没法及时看到更新后的效果。比如你修改了一个文件，已经 update 到 tomcat 了，但刷新页面还是之前的页面，就是因为缓存引起的。

## 访问静态页面

这个和 Thymeleaf 没啥关系，应该说是通用的，我把它一并写到这里的原因是一般我们做网站的时候，都会做一个 404 页面和 500 页面，为了出错时给用户一个友好的展示，而不至于一堆异常信息抛出来。Spring Boot 中会自动识别模板目录（templates/）下的 404.html 和 500.html 文件。我们在 templates/ 目录下新建一个 error 文件夹，专门放置错误的 html 页面，然后分别打印些信息。以 404.html 为例：

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    这是404页面
  </body>
</html>
```

我们再写一个 controller 来测试一下 404 和 500 页面：

```java
@Controller
@RequestMapping("/thymeleaf")
public class ThymeleafController {
    @RequestMapping("/test404")
    public String test404() {
        return "index";
    }
    @RequestMapping("/test500")
    public String test500() {
        int i = 1 / 0;
        return "index";
    }
}
```

当我们在浏览器中输入 localhost:8080/thymeleaf/test400 时，故意输入错误，找不到对应的方法，就会跳转到 404.html 显示。当我们在浏览器中输入 localhost:8088/thymeleaf/test505 时，会抛出异常，然后会自动跳转到 500.html 显示。

## Thymeleaf 中处理对象

我们来看一下 thymeleaf 模板中如何处理对象信息，假如我们在做个人博客的时候，需要给前端传博主相关信息来展示，那么我们会封装成一个博主对象，比如：

```java

public class Blogger {
    private Long id;
    private String name;
    private String pass;
	// 省去set和get
}
```

然后在 controller 层中初始化一下：

```java

@GetMapping("/getBlogger")
public String getBlogger(Model model) {
	Blogger blogger = new Blogger(1L, "xxx", "123456");
	model.addAttribute("blogger", blogger);
	return "blogger";
}
```

我们先初始化一个 Blogger 对象，然后将该对象放到 Model 中，然后返回到 blogger.html 页面去渲染。接下来我们再写一个 blogger.html 来渲染 blogger 信息：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <title>博主信息</title>
    </head>
    <body>
      <form action="" th:object="${blogger}">
        用户编号：<input name="id" th:value="${blogger.id}" /><br />
        用户姓名：<input
          type="text"
          name="username"
          th:value="${blogger.getName()}"
        /><br />
        登陆密码：<input type="text" name="password" th:value="*{pass}" />
      </form>
    </body>
  </html>
</html>
```

可以看出，在 thymeleaf 模板中，使用 th:object="${}" 来获取对象信息，然后在表单里面可以有三种方式来获取对象属性。如下：

- 使用 `th:value="*{属性名}"`
- 使用 th:value="${对象.属性名}"，对象指的是上面使用 th:object 获取的对象
- 使用 th:value="${对象.get 方法}"，对象指的是上面使用 th:object 获取的对象

## Thymeleaf 中处理 List

处理 List 的话，和处理上面介绍的对象差不多，但是需要在 thymeleaf 中进行遍历。我们先在 Controller 中模拟一个 List。

```
@GetMapping("/getList")
public String getList(Model model) {
    Blogger blogger1 = new Blogger(1L, "xxx", "123456");
    Blogger blogger2 = new Blogger(2L, "达人课", "123456");
    List<Blogger> list = new ArrayList<>();
    list.add(blogger1);
    list.add(blogger2);
    model.addAttribute("list", list);
    return "list";
}
```

接下来我们写一个 list.html 来获取该 list 信息，然后在 list.html 中遍历这个 list。如下：

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>博主信息</title>
</head>
<body>
<form action="" th:each="blogger : ${list}" >
    用户编号：<input name="id" th:value="${blogger.id}"/><br>
    用户姓名：<input type="text" name="password" th:value="${blogger.name}"/><br>
    登录密码：<input type="text" name="username" th:value="${blogger.getPass()}"/>
</form>
</body>
</html>
```

可以看出，其实和处理单个对象信息差不多，Thymeleaf 使用 `th:each` 进行遍历，`${}` 取 model 中传过来的参数，然后自定义 list 中取出来的每个对象，这里定义为 blogger。表单里面可以直接使用 `${对象.属性名}` 来获取 list 中对象的属性值，也可以使用 `${对象.get方法}` 来获取，这点和上面处理对象信息是一样的，但是不能使用 `*{属性名}` 来获取对象中的属性，thymeleaf 模板获取不到。

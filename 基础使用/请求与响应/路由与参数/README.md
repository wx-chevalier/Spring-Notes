# 路由与参数

# 路由

## 路径匹配

@RequestMapping 是 Spring MVC 中最常用的注解之一，`org.springframework.web.bind.annotation.RequestMapping` 被用于将某个请求映射到具体的处理类或者方法中：

```java
// @RequestMapping with Class
@Controller
@RequestMapping("/home")
public class HomeController {}

// @RequestMapping with Method
@RequestMapping(value="/method0")
@ResponseBody
public String method0(){
    return "method0";
}

// @RequestMapping with Multiple URI
@RequestMapping(value={"/method1","/method1/second"})
@ResponseBody
public String method1(){
    return "method1";
}

// @RequestMapping with HTTP Method
@RequestMapping(value="/method3", method={RequestMethod.POST,RequestMethod.GET})
@ResponseBody
public String method3(){
    return "method3";
}

// @RequestMapping default method
@RequestMapping()
@ResponseBody
public String defaultMethod(){
    return "default method";
}

// @RequestMapping fallback method
@RequestMapping("*")
@ResponseBody
public String fallbackMethod(){
    return "fallback method";
}

// @RequestMapping headers
@RequestMapping(value="/method5", headers={"name=pankaj", "id=1"})
@ResponseBody
public String method5(){
    return "method5";
}

// 表示将功能处理方法将生产 json 格式的数据，此时根据请求头中的 Accept 进行匹配，如请求头 Accept:application/json 时即可匹配;
@RequestMapping(value = "/produces", produces = "application/json")
@RequestMapping(produces={"text/html", "application/json"})
```

## 路由日志

该 Spring Boot 2.1.x 版本开始，将这些日志的打印级别做了调整：从原来的 INFO 调整为 TRACE。所以，当我们希望在应用启动的时候打印这些信息的话，只需要在配置文件增增加对 RequestMappingHandlerMapping 类的打印级别设置即可，比如在 application.properties 中增加下面这行配置：

```sh
logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping=trace
```

在增加了上面的配置之后重启应用，便可以看到如下的日志打印：

```sh
2020-02-11 15:36:09.787 TRACE 49215 --- [main] s.w.s.m.m.a.RequestMappingHandlerMapping :
	c.d.c.UserController:
	{PUT /users/{id}}: putUser(Long,User)
	{GET /users/{id}}: getUser(Long)
	{POST /users/}: postUser(User)
	{GET /users/}: getUserList()
	{DELETE /users/{id}}: deleteUser(Long)
2020-02-11 15:36:09.791 TRACE 49215 --- [main] s.w.s.m.m.a.RequestMappingHandlerMapping :
	o.s.b.a.w.s.e.BasicErrorController:
	{ /error}: error(HttpServletRequest)
	{ /error, produces [text/html]}: errorHtml(HttpServletRequest,HttpServletResponse)
2020-02-11 15:36:09.793 DEBUG 49215 --- [main] s.w.s.m.m.a.RequestMappingHandlerMapping : 7 mappings in 'requestMappingHandlerMapping'

```

可以看到在 2.1.x 版本之后，除了调整了日志级别之外，对于打印内容也做了调整。现在的打印内容根据接口创建的 Controller 类做了分类打印，这样更有助于开发者根据自己编写的 Controller 来查找初始化了那些 HTTP 接口。

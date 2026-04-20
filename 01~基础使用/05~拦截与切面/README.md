# AOP

AOP：Aspect Oriented Programming 的缩写，意为：面向切面编程。面向切面编程的目标就是分离关注点。使用 AOP，首先需要引入 AOP 的依赖。

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

过滤器依赖于 servlet 容器。在实现上基于函数回调，可以对几乎所有请求进行过滤，但是缺点是一个过滤器实例只能在容器初始化时调用一次。使用过滤器的目的是用来做一些过滤操作，获取我们想要获取的数据，比如：在过滤器中修改字符编码；在过滤器中修改 HttpServletRequest 的一些参数，包括：过滤低俗文字、危险字符等。拦截器依赖于 web 框架，在 SpringMVC 中就是依赖于 SpringMVC 框架。在实现上基于 Java 的反射机制，属于面向切面编程（AOP）的一种运用。由于拦截器是基于 web 框架的调用，因此可以使用 Spring 的依赖注入（DI）进行一些业务操作，同时一个拦截器实例在一个 controller 生命周期之内可以多次调用。但是缺点是只能对 controller 请求进行拦截，对其他的一些比如直接访问静态资源的请求则没办法进行拦截处理。总结而言：

- Filter 是依赖于 Servlet 容器，属于 Servlet 规范的一部分，而拦截器则是独立存在的，可以在任何情况下使用。
- Filter 的执行由 Servlet 容器回调完成，而拦截器通常通过动态代理的方式来执行。
- Filter 的生命周期由 Servlet 容器管理，而拦截器则可以通过 IoC 容器来管理，因此可以通过注入等方式来获取其他 Bean 的实例，因此使用会更方便。

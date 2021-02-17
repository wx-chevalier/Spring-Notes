# AOP

AOP：Aspect Oriented Programming 的缩写，意为：面向切面编程。面向切面编程的目标就是分离关注点。使用 AOP，首先需要引入 AOP 的依赖。

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

# 快速开始

Spring Boot 中使用 AOP 非常简单，假如我们要在项目中打印一些 log，在引入了上面的依赖之后，我们新建一个类 LogAspectHandler，用来定义切面和处理方法。只要在类上加个@Aspect 注解即可。@Aspect 注解用来描述一个切面类，定义切面类的时候需要打上这个注解。@Component 注解让该类交给 Spring 来管理。

```java
@Aspect
@Component
public class LogAspectHandler {

}
```

这里主要介绍几个常用的注解及使用：

- @Pointcut：定义一个切面，即上面所描述的关注的某件事入口。
- @Before：在做某件事之前做的事。
- @After：在做某件事之后做的事。
- @AfterReturning：在做某件事之后，对其返回值做增强处理。
- @AfterThrowing：在做某件事抛出异常时，处理。

## @Pointcut 注解

@Pointcut 注解：用来定义一个切面（切入点），即上文中所关注的某件事情的入口。切入点决定了连接点关注的内容，使得我们可以控制通知什么时候执行。

```java
@Aspect
@Component
public class LogAspectHandler {

    /**
     * 定义一个切面，拦截com.test.controller包和子包下的所有方法
     */
    @Pointcut("execution(* com.test.controller..*.*(..))")
    public void pointCut() {}
}
```

@Pointcut 注解指定一个切面，定义需要拦截的东西，这里介绍两个常用的表达式：一个是使用 execution()，另一个是使用 annotation()。以 `execution(*com.test.controller.*.*(..)))` 表达式为例，语法如下：

- `execution()` 为表达式主体，第一个 `*` 号的位置：表示返回值类型，`*` 表示所有类型。
- 包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，`com.test.controller` 包、子包下所有类的方法
- 第二个 `*` 号的位置：表示类名，`*` 表示所有类
- `*(..)` ：这个星号表示方法名，`*` 表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数

annotation() 方式是针对某个注解来定义切面，比如我们对具有 @GetMapping 注解的方法做切面，可以如下定义切面：

```java
@Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
public void annotationCut() {}
```

然后使用该切面的话，就会切入注解是 @GetMapping 的方法。因为在实际项目中，可能对于不同的注解有不同的逻辑处理，比如 @GetMapping、@PostMapping、@DeleteMapping 等。所以这种按照注解的切入方式在实际项目中也很常用。

## @Before 注解

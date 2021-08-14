# Async Support

在 Spring Boot 中，我们可以通过 Java 注解的方式来启用异步处理：

```java
@Configuration
@EnableAsync
public class SpringAsyncConfig { ... }
```

或者以 XML 的方式启用：

```xml
<task:executor id="myexecutor" pool-size="5"  />
<task:annotation-driven executor="myexecutor"/>
```

# @Async

@Async 注解只能作用于 public 方法，并且不能够自调用。

```java
@Async
public void asyncMethodWithVoidReturnType() {
    System.out.println("Execute method asynchronously. "
      + Thread.currentThread().getName());
}

@Async
public Future<String> asyncMethodWithReturnType() {
    System.out.println("Execute method asynchronously - "
      + Thread.currentThread().getName());
    try {
        Thread.sleep(5000);
        return new AsyncResult<String>("hello world !!!!");
    } catch (InterruptedException e) {
        //
    }

    return null;
}
```

Spring 还提供了一个实现 Future 的 AsyncResult 类。这可以用来跟踪异步方法执行的结果。现在，让我们调用上述方法，并使用 Future 对象检索异步过程的结果。

```java
public void testAsyncAnnotationForMethodsWithReturnType()
  throws InterruptedException, ExecutionException {
    System.out.println("Invoking an asynchronous method. "
      + Thread.currentThread().getName());
    Future<String> future = asyncAnnotationExample.asyncMethodWithReturnType();

    while (true) {
        if (future.isDone()) {
            System.out.println("Result from asynchronous process - " + future.get());
            break;
        }
        System.out.println("Continue doing something else. ");
        Thread.sleep(1000);
    }
}
```

# Executor

默认情况下，Spring 使用 SimpleAsyncTaskExecutor 实际异步运行这些方法。可以在两个级别上覆盖默认值：在应用程序级别或单个方法级别。

## 方法级别复写

所需的执行程序需要在配置类中声明：

```java
@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}

@Async("threadPoolTaskExecutor")
public void asyncMethodWithConfiguredExecutor() {
    System.out.println("Execute method with configured executor - "
      + Thread.currentThread().getName());
}
```

## 应用级别复写

配置类应实现 AsyncConfigurer 接口，这意味着它具有实现 getAsyncExecutor() 方法。在这里，我们将返回整个应用程序的执行程序–现在，它成为运行以 @Async 注释的方法的默认执行程序：

```java
@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    return new ThreadPoolTaskExecutor();
  }
}
```

# 异常处理

当方法返回类型为 Future 时，异常处理很容易：Future.get() 方法将引发异常。但是，如果返回类型为 void，则异常不会传播到调用线程；因此，我们需要添加额外的配置来处理异常。我们将通过实现 AsyncUncaughtExceptionHandler 接口来创建自定义异步异常处理程序。当存在任何未捕获的异步异常时，将调用 handleUncaughtException() 方法：

```java
public class CustomAsyncExceptionHandler
  implements AsyncUncaughtExceptionHandler {

  @Override
  public void handleUncaughtException(
    Throwable throwable,
    Method method,
    Object... obj
  ) {
    System.out.println("Exception message - " + throwable.getMessage());
    System.out.println("Method name - " + method.getName());
    for (Object param : obj) {
      System.out.println("Parameter value - " + param);
    }
  }
}
```

在上一节中，我们介绍了由配置类实现的 AsyncConfigurer 接口。作为其中的一部分，我们还需要重写 getAsyncUncaughtExceptionHandler() 方法以返回我们的自定义异步异常处理程序：

```java
@Override
public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new CustomAsyncExceptionHandler();
}
```

# Links



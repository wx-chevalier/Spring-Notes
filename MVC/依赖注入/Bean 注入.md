# Bean 注入

# Autowired

## 多线程注入

在多线程下如果使用 Autowired 来进行依赖注入，可能会出现 Null 异常，譬如如下代码：

```java
public class UserThreadTask implements Runnable {
  @Autowired
  private UserThreadService userThreadService;

  @Override
  public void run() {
    AdeUser user = userThreadService.get("0");
    System.out.println(user);
  }
}
```

造成这种注入失败的原因就是 spring 和多线程的安全问题，不支持这样的注入方式。我们可以通过构造函数传入到多线程环境中：

```java
public class UserThreadTask implements Runnable {
  private UserThreadService userThreadService;

  public UserThreadTask(UserThreadService userThreadService) {
    this.userThreadService = userThreadService;
  }

  @Override
  public void run() {
    AdeUser user = userThreadService.get("0");
    System.out.println(user);
  }
}
```

调用方式如下：

```java
Thread t = new Thread(new UserThreadTask(userThreadService));
t.start();
```

我们也可以通过 ApplicaContext 获取所需的 Service：

```java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextHolder implements ApplicationContextAware {
  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext context)
    throws BeansException {
    ApplicationContextHolder.context = context;
  }

  // 根据 Bean name 获取实例
  public static Object getBeanByName(String beanName) {
    if (beanName == null || context == null) {
      return null;
    }
    return context.getBean(beanName);
  }

  // 只适合一个 class 只被定义一次的 bean（也就是说，根据 class 不能匹配出多个该 class 的实例）
  public static Object getBeanByType(Class clazz) {
    if (clazz == null || context == null) {
      return null;
    }
    return context.getBean(clazz);
  }

  public static String[] getBeanDefinitionNames() {
    return context.getBeanDefinitionNames();
  }
}
```

调用方式如下：

```java
UserService user = (UserService) ApplicationContextHolder.getBeanByName("userService");
```

这种方式不管是否为多线程，还是不接收 Spring 管理的类，都可以用这种方式获得 spring 管理的类。

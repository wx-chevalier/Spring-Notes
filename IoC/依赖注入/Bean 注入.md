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

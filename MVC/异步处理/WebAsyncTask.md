# Callable

```java
@ResponseBody
@GetMapping("/callable")
public Callable<String> helloGetCallable() throws Exception {
System.out.println(Thread.currentThread().getName() + " main thread start");

Callable<String> callable =
    () -> {
        System.out.println(Thread.currentThread().getName() + " child thread start");
        TimeUnit.SECONDS.sleep(25); // 模拟处理业务逻辑，话费了5秒钟
        System.out.println(Thread.currentThread().getName() + " child thread end");

        // 这里稍微小细节一下：最终返回的不是Callable对象，而是它里面的内容
        return "hello world";
    };

System.out.println(Thread.currentThread().getName() + " main thread end");
return callable;
}
```

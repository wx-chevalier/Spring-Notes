package config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@Slf4j
public class Application {

  Logger logger = LoggerFactory.getLogger(Application.class);

  @RequestMapping("/")
  public String home() throws InterruptedException {

    TimeUnit.SECONDS.sleep(10);

    System.out.println("Completion");

    return "Hello Spring World";
  }

  @RequestMapping("/log")
  public String index() {
    logger.trace("A TRACE Message");
    logger.debug("A DEBUG Message");
    logger.info("An INFO Message");
    logger.warn("A WARN Message");
    logger.error("An ERROR Message");

    return "Howdy! Check out the Logs to see the output...";
  }

  @ResponseBody
  @GetMapping("/callable")
  public Callable<String> helloGetCallable() throws Exception {
    System.out.println(Thread.currentThread().getName() + " main thread start");

    Callable<String> callable =
        () -> {
          System.out.println(Thread.currentThread().getName() + " child thread start");
          TimeUnit.SECONDS.sleep(5); // 模拟处理业务逻辑，花费了5秒钟
          System.out.println(Thread.currentThread().getName() + " child thread end");

          // 这里稍微小细节一下：最终返回的不是Callable对象，而是它里面的内容
          return "hello world";
        };

    System.out.println(Thread.currentThread().getName() + " main thread end");
    return callable;
  }

  @ResponseBody
  @GetMapping("/async_task")
  public WebAsyncTask<String> helloGetWebAsyncTask() throws Exception {
    System.out.println(Thread.currentThread().getName() + " main thread start");

    Callable<String> callable =
        () -> {
          System.out.println(Thread.currentThread().getName() + " child thread start");

          if (Math.random() < 0.5) {
            throw new Exception("Exception Response");
          }

          TimeUnit.SECONDS.sleep(600); // 模拟处理业务逻辑，话费了5秒钟
          System.out.println(Thread.currentThread().getName() + " child thread end");

          return "hello world";
        };

    // 采用WebAsyncTask 返回 这样可以处理超时和错误 同时也可以指定使用的Excutor名称
    WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(3000, callable);
    // 注意：onCompletion表示完成，不管你是否超时、是否抛出异常，这个函数都会执行的
    webAsyncTask.onCompletion(() -> System.out.println("Completion"));

    // 这两个返回的内容，最终都会放进response里面去===========
    webAsyncTask.onTimeout(() -> "Timeout");

    // 备注：这个是Spring5新增的
    webAsyncTask.onError(() -> "Exception");

    System.out.println(Thread.currentThread().getName() + " main thread end");
    return webAsyncTask;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}

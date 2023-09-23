# Spring Boot 中日志处理

日志处理是服务端开发中的常见需求，其能够帮助我们对系统的运行情况进行实时监控，以及及时地排查、解决系统中存在的问题。在开发中，我们经常使用 System.out.println() 来打印一些信息，但是这样不好，因为大量的使用 System.out 会增加资源的消耗。

Spring Boot 中日志的用法与其他并无差异，直接获取 logger 对象并使用，Spring Boot 预置了 Logback 的配置。

```java
@RestController
public class LoggingController {
  Logger logger = LoggerFactory.getLogger(LoggingController.class);

  @RequestMapping("/")
  public String index() {
    logger.trace("A TRACE Message");
    logger.debug("A DEBUG Message");
    logger.info("An INFO Message");
    logger.warn("A WARN Message");
    logger.error("An ERROR Message");

    return "Howdy! Check out the Logs to see the output...";
  }
}
```

Spring Boot 内置的日志级别是 Info，如果我们需要打印 Debug 或者 Trace 级别的日志，可以添加环境变量：

```sh
$ mvn spring-boot:run
  -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE

$ ./gradlew bootRun -Pargs=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE
```

也可以通过修改 application.properties 文件：

```sh
logging.level.root=WARN
logging.level.com.baeldung=TRACE
```

在实际的工程中，我们需要严格规范日志输出等级，影响业务进行或意料外异常输出 Error 级别，并统一输出到独立文件，接入系统错误监控告警。不过 Error 级别的日志也要不断地进行优化降噪，以保证及时有效地人为介入处理。对于接口层，则需要统一拦截捕获，避免异常向外系统传播，自身系统无法感知问题；服务层中则应该统一日志输出，包括耗时、接口成功标识、业务成功标识，为监控做准备。并且应该统一输出日志的 traceId，以方便进行分布式追踪，可以通过扩展 `ch.qos.logback.classic.pattern.ClassicConverter` 来实现自动输出。

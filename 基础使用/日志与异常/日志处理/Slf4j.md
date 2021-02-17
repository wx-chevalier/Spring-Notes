# Slf4j

SLF4J，即简单日志门面（Simple Logging Facade for Java），不是具体的日志解决方案，它只服务于各种各样的日志系统。按照官方的说法，SLF4J 是一个用于日志系统的简单 Facade，允许最终用户在部署其应用时使用其所希望的日志系统。

![](http://www.slf4j.org/images/concrete-bindings.png)

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {

  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

Spring Boot 对 slf4j 支持的很好，内部已经集成了 slf4j，一般我们在使用的时候，会对 slf4j 做一下配置。`application.yml` 文件是 Spring Boot 中唯一一个需要配置的文件，一开始创建工程的时候是 `application.properties` 文件，个人比较细化用 yml 文件，因为 yml 文件的层次感特别好，看起来更直观，但是 yml 文件对格式要求比较高，比如英文冒号后面必须要有个空格，否则项目估计无法启动，而且也不报错。用 properties 还是 yml 视个人习惯而定，都可以。本课程使用 yml。

我们看一下 application.yml 文件中对日志的配置：

```
logging:
  config: logback.xml
  level:
    com.itcodai.course03.dao: trace
```

`logging.config` 是用来指定项目启动的时候，读取哪个配置文件，这里指定的是日志配置文件是根路径下的 `logback.xml` 文件，关于日志的相关配置信息，都放在 `logback.xml` 文件中了。`logging.level` 是用来指定具体的 mapper 中日志的输出级别，上面的配置表示 `com.itcodai.course03.dao` 包下的所有 mapper 日志输出级别为 trace，会将操作数据库的 sql 打印出来，开发时设置成 trace 方便定位问题，在生产环境上，将这个日志级别再设置成 error 级别即可。

常用的日志级别按照从高到低依次为：ERROR、WARN、INFO、DEBUG。

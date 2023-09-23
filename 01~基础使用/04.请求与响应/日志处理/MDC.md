# MDC

MDC（Mapped Diagnostic Context，映射调试上下文）是 log4j 和 logback 提供的一种方便在多线程条件下记录日志的功能。某些应用程序采用多线程的方式来处理多个用户的请求。在一个用户的使用过程中，可能有多个不同的线程来进行处理。典型的例子是 Web 应用服务器。当用户访问某个页面时，应用服务器可能会创建一个新的线程来处理该请求，也可能从线程池中复用已有的线程。在一个用户的会话存续期间，可能有多个线程处理过该用户的请求。这使得比较难以区分不同用户所对应的日志。当需要追踪某个用户在系统中的相关日志记录时，就会变得很麻烦。

一种解决的办法是采用自定义的日志格式，把用户的信息采用某种方式编码在日志记录中。这种方式的问题在于要求在每个使用日志记录器的类中，都可以访问到用户相关的信息。这样才可能在记录日志时使用。这样的条件通常是比较难以满足的。MDC 的作用是解决这个问题。MDC 可以看成是一个与当前线程绑定的哈希表，可以往其中添加键值对。MDC 中包含的内容可以被同一线程中执行的代码所访问。当前线程的子线程会继承其父线程中的 MDC 的内容。当需要记录日志时，只需要从 MDC 中获取所需的信息即可。MDC 的内容则由程序在适当的时候保存进去。对于一个 Web 应用来说，通常是在请求被处理的最开始保存这些数据。

# 使用案例

让我们从一个例子开始。假设我们要写一个转账的软件。我们设置了一个 Transfer 类来表示一些基本信息：一个独特的转账 ID 和发送者的名字。

```java
public class Transfer {
    private String transactionId;
    private String sender;
    private Long amount;

    public Transfer(String transactionId, String sender, long amount) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.amount = amount;
    }
}
```

为了执行转移，我们需要使用一个由简单 API 支持的服务。

```java
public abstract class TransferService {

    public boolean transfer(long amount) {
        // connects to the remote service to actually transfer money
    }

    abstract protected void beforeTransfer(long amount);

    abstract protected void afterTransfer(long amount, boolean outcome);
}
```

beforeTransfer() 和 afterTransfer() 方法可以被重写，以便在传输完成之前和之后运行自定义代码。我们将利用 beforeTransfer() 和 afterTransfer() 来记录一些关于传输的信息。

```java
import org.apache.log4j.Logger;
import com.baeldung.mdc.TransferService;

public class Log4JTransferService extends TransferService {
    private Logger logger = Logger.getLogger(Log4JTransferService.class);

    @Override
    protected void beforeTransfer(long amount) {
        logger.info("Preparing to transfer " + amount + "$.");
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        logger.info(
          "Has transfer of " + amount + "$ completed successfully ? " + outcome + ".");
    }
}
```

这里需要注意的主要问题是，当创建日志信息时，不可能访问 Transfer 对象；只有金额可以访问，因此不可能记录交易 ID 或发件人。让我们设置通常的 log4j.properties 文件，以便在控制台记录。

```yml
log4j.appender.consoleAppender=org.apache.log4j.ConsoleAppender
log4j.appender.consoleAppender.layout=org.apache.log4j.PatternLayout
log4j.appender.consoleAppender.layout.ConversionPattern=%-4r [%t] %5p %c %x - %m%n
log4j.rootLogger = TRACE, consoleAppender
```

最后让我们设置一个小程序，它能够通过 ExecutorService 同时运行多个传输。

```java
public class TransferDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        TransactionFactory transactionFactory = new TransactionFactory();
        for (int i = 0; i < 10; i++) {
            Transfer tx = transactionFactory.newInstance();
            Runnable task = new Log4JRunnable(tx);
            executor.submit(task);
        }
        executor.shutdown();
    }
}
```

我们注意到，为了使用 ExecutorService，我们需要将 Log4JTransferService 的执行包装在一个适配器中，因为 executor.submit()期望有一个 Runnable。

```java
public class Log4JRunnable implements Runnable {
    private Transfer tx;

    public Log4JRunnable(Transfer tx) {
        this.tx = tx;
    }

    public void run() {
        log4jBusinessService.transfer(tx.getAmount());
    }
}
```

当我们运行同时管理多笔转账的演示程序时，我们很快发现，日志并不像我们希望的那样有用。跟踪每笔转账的执行情况是很复杂的，因为被记录的唯一有用的信息是转账的金额和执行该特定转账的线程的名称。更重要的是，我们不可能区分由同一个线程执行的相同金额的两个不同交易，因为相关的日志行看起来基本相同。

## Log4j

Log4j 中的 MDC 允许我们在一个类似于地图的结构中填入一些信息，这些信息在实际写入日志消息时可以被 appender 访问。MDC 结构在内部被附加到执行线程上，与 ThreadLocal 变量的方式相同。

```java
import org.apache.log4j.MDC;

public class Log4JRunnable implements Runnable {
    private Transfer tx;
    private static Log4JTransferService log4jBusinessService = new Log4JTransferService();

    public Log4JRunnable(Transfer tx) {
        this.tx = tx;
    }

    public void run() {
        MDC.put("transaction.id", tx.getTransactionId());
        MDC.put("transaction.owner", tx.getSender());
        log4jBusinessService.transfer(tx.getAmount());
        MDC.clear();
    }
}
```

不出所料，MDC.put() 被用来在 MDC 中添加一个键和一个相应的值，而 MDC.clear() 则清空 MDC。现在让我们修改 log4j.properties 来打印我们刚刚存储在 MDC 中的信息。只需改变转换模式，用 %X{} 占位符来表示我们希望被记录的 MDC 中的每个条目。

```yaml
log4j.appender.consoleAppender.layout.ConversionPattern=
%-4r [%t] %5p %c{1} %x - %m - tx.id=%X{transaction.id} tx.owner=%X{transaction.owner}%n
```

现在，如果我们运行这个应用程序，我们会注意到每一行都带有正在处理的事务的信息，使我们更容易跟踪应用程序的执行。

```log
638  [pool-1-thread-2]  INFO Log4JBusinessService
  - Has transfer of 1104$ completed successfully ? true. - tx.id=2 tx.owner=Marc
638  [pool-1-thread-2]  INFO Log4JBusinessService
  - Preparing to transfer 1685$. - tx.id=4 tx.owner=John
666  [pool-1-thread-1]  INFO Log4JBusinessService
  - Has transfer of 1985$ completed successfully ? true. - tx.id=1 tx.owner=Marc
666  [pool-1-thread-1]  INFO Log4JBusinessService
  - Preparing to transfer 958$. - tx.id=5 tx.owner=Susan
739  [pool-1-thread-3]  INFO Log4JBusinessService
  - Has transfer of 783$ completed successfully ? true. - tx.id=3 tx.owner=Samantha
739  [pool-1-thread-3]  INFO Log4JBusinessService
  - Preparing to transfer 1024$. - tx.id=6 tx.owner=John
1259 [pool-1-thread-2]  INFO Log4JBusinessService
  - Has transfer of 1685$ completed successfully ? false. - tx.id=4 tx.owner=John
1260 [pool-1-thread-2]  INFO Log4JBusinessService
  - Preparing to transfer 1667$. - tx.id=7 tx.owner=Marc
```

## Log4j2

Log4j2 中也有同样的功能，让我们看看如何使用它。首先让我们建立一个 TransferService 子类，使用 Log4j2 进行记录。

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4J2TransferService extends TransferService {
    private static final Logger logger = LogManager.getLogger();

    @Override
    protected void beforeTransfer(long amount) {
        logger.info("Preparing to transfer {}$.", amount);
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        logger.info("Has transfer of {}$ completed successfully ? {}.", amount, outcome);
    }
}
```

然后让我们改变使用 MDC 的代码，它在 Log4j2 中实际上叫做 ThreadContext。

```java
import org.apache.log4j.MDC;

public class Log4J2Runnable implements Runnable {
    private final Transaction tx;
    private Log4J2BusinessService log4j2BusinessService = new Log4J2BusinessService();

    public Log4J2Runnable(Transaction tx) {
        this.tx = tx;
    }

    public void run() {
        ThreadContext.put("transaction.id", tx.getTransactionId());
        ThreadContext.put("transaction.owner", tx.getOwner());
        log4j2BusinessService.transfer(tx.getAmount());
        ThreadContext.clearAll();
    }
}
```

同样，ThreadContext.put() 在 MDC 中添加了一个条目，而 ThreadContext.clearAll() 则删除了所有现有条目。我们仍然想念 log4j2.xml 文件来配置日志记录。我们可以注意到，指定哪些 MDC 条目应该被记录的语法与 Log4j 中使用的语法相同。

```xml
<Configuration status="INFO">
    <Appenders>
        <Console name="stdout" target="SYSTEM_OUT">
            <PatternLayout
              pattern="%-4r [%t] %5p %c{1} - %m - tx.id=%X{transaction.id} tx.owner=%X{transaction.owner}%n" />
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="com.baeldung.log4j2" level="TRACE" />
        <AsyncRoot level="DEBUG">
            <AppenderRef ref="stdout" />
        </AsyncRoot>
    </Loggers>
</Configuration>
```

再次，让我们执行应用程序，我们将看到 MDC 信息被打印在日志中。

```log
1119 [pool-1-thread-3]  INFO Log4J2BusinessService
  - Has transfer of 1198$ completed successfully ? true. - tx.id=3 tx.owner=Samantha
1120 [pool-1-thread-3]  INFO Log4J2BusinessService
  - Preparing to transfer 1723$. - tx.id=5 tx.owner=Samantha
1170 [pool-1-thread-2]  INFO Log4J2BusinessService
  - Has transfer of 701$ completed successfully ? true. - tx.id=2 tx.owner=Susan
1171 [pool-1-thread-2]  INFO Log4J2BusinessService
  - Preparing to transfer 1108$. - tx.id=6 tx.owner=Susan
1794 [pool-1-thread-1]  INFO Log4J2BusinessService
  - Has transfer of 645$ completed successfully ? true. - tx.id=4 tx.owner=Susan
```

## Slf4j/Logback

在 Slf4j 中，MDC 也是可用的，条件是底层日志库支持它。正如我们刚才看到的，Logback 和 Log4j 都支持 MDC，所以我们不需要什么特别的东西就可以在标准的设置下使用它。让我们准备一下通常的 TransferService 子类，这次使用 Java 的 Simple Logging Facade。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Slf4TransferService extends TransferService {
    private static final Logger logger = LoggerFactory.getLogger(Slf4TransferService.class);

    @Override
    protected void beforeTransfer(long amount) {
        logger.info("Preparing to transfer {}$.", amount);
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        logger.info("Has transfer of {}$ completed successfully ? {}.", amount, outcome);
    }
}
```

现在让我们使用 SLF4J 的 MDC 味道。在这种情况下，其语法和语义与 log4j 中的相同。

```java
import org.slf4j.MDC;

public class Slf4jRunnable implements Runnable {
    private final Transaction tx;

    public Slf4jRunnable(Transaction tx) {
        this.tx = tx;
    }

    public void run() {
        MDC.put("transaction.id", tx.getTransactionId());
        MDC.put("transaction.owner", tx.getOwner());
        new Slf4TransferService().transfer(tx.getAmount());
        MDC.clear();
    }
}
```

我们必须提供 Logback 的配置文件：logback.xml。

```xml
<configuration>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%-4r [%t] %5p %c{1} - %m - tx.id=%X{transaction.id} tx.owner=%X{transaction.owner}%n</pattern>
	</encoder>
    </appender>
    <root level="TRACE">
        <appender-ref ref="stdout" />
    </root>
</configuration>
```

同样，我们将看到 MDC 中的信息被正确地添加到了日志信息中，尽管这些信息并没有在 log.info() 方法中明确提供。

```log
1020 [pool-1-thread-3]  INFO c.b.m.s.Slf4jBusinessService
  - Has transfer of 1869$ completed successfully ? true. - tx.id=3 tx.owner=John
1021 [pool-1-thread-3]  INFO c.b.m.s.Slf4jBusinessService
  - Preparing to transfer 1303$. - tx.id=6 tx.owner=Samantha
1221 [pool-1-thread-1]  INFO c.b.m.s.Slf4jBusinessService
  - Has transfer of 1498$ completed successfully ? true. - tx.id=4 tx.owner=Marc
1221 [pool-1-thread-1]  INFO c.b.m.s.Slf4jBusinessService
  - Preparing to transfer 1528$. - tx.id=7 tx.owner=Samantha
1492 [pool-1-thread-2]  INFO c.b.m.s.Slf4jBusinessService
  - Has transfer of 1110$ completed successfully ? true. - tx.id=5 tx.owner=Samantha
1493 [pool-1-thread-2]  INFO c.b.m.s.Slf4jBusinessService
  - Preparing to transfer 644$. - tx.id=8 tx.owner=John
```

# MDC and Thread Pools

**MDC implementations are usually using \*ThreadLocal\*s to store the contextual information.** That's an easy and reasonable way to achieve thread-safety. However, we should be careful using MDC with thread pools.

Let's see how the combination of _ThreadLocal_-based MDCs and thread pools can be dangerous:

1. We get a thread from the thread pool.
2. Then we store some contextual information in MDC using _MDC.put()_ or _ThreadContext.put()_.
3. We use this information in some logs and somehow we forgot to clear the MDC context.
4. The borrowed thread comes back to the thread pool.
5. After a while, the application gets the same thread from the pool.
6. Since we didn't clean up the MDC last time, this thread still owns some data from the previous execution.

This may cause some unexpected inconsistencies between executions. **One way to prevent this is to always remember to clean up the MDC context at the end of each execution.** This approach usually needs rigorous human supervision and, therefore, is error-prone.

**Another approach is to use \*ThreadPoolExecutor\* hooks and perform necessary cleanups after each execution.** To do that, we can extend the _ThreadPoolExecutor_ class and override the _afterExecute()_ hook:

```java
public class MdcAwareThreadPoolExecutor extends ThreadPoolExecutor {

    public MdcAwareThreadPoolExecutor(int corePoolSize,
      int maximumPoolSize,
      long keepAliveTime,
      TimeUnit unit,
      BlockingQueue<Runnable> workQueue,
      ThreadFactory threadFactory,
      RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        System.out.println("Cleaning the MDC context");
        MDC.clear();
        org.apache.log4j.MDC.clear();
        ThreadContext.clearAll();
    }
}
```

This way, the MDC cleanup would happen after each normal or exceptional execution automatically. So, there is no need to do it manually:

```java
@Override
public void run() {
    MDC.put("transaction.id", tx.getTransactionId());
    MDC.put("transaction.owner", tx.getSender());

    new Slf4TransferService().transfer(tx.getAmount());
}
```

Now we can re-write the same demo with our new executor implementation:

```java
ExecutorService executor = new MdcAwareThreadPoolExecutor(3, 3, 0, MINUTES,
  new LinkedBlockingQueue<>(), Thread::new, new AbortPolicy());

TransactionFactory transactionFactory = new TransactionFactory();

for (int i = 0; i < 10; i++) {
    Transfer tx = transactionFactory.newInstance();
    Runnable task = new Slf4jRunnable(tx);

    executor.submit(task);
}

executor.shutdown();
```

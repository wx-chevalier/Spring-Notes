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

Log4j 中的 MDC 允许我们在一个类似于地图的结构中填入一些信息，这些信息在实际写入日志消息时可以被 appender 访问。

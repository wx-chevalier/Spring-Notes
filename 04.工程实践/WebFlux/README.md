# Web Flux

Reactive Programming 在过去早已有之，并不是什么新鲜事物。但是在最近几年，它似乎有着越来越流行的趋势。近期，Java 技术圈围绕着 Reactive Programming 这一主题，推出了许许多多的新版本工具，让人感到眼花缭乱。本文首先选择其中的几项重点更新内容，梳理一下它们之间的关系。

- Spring Boot 2.0：Spring Boot 2.0 are now offering first-class support for developing reactive applications, via auto-configuration and starter-POMs。一如既往体现着 Spring Boot 配置简便的特点，只需几处简单配置就可以开发 reactive 应用了。围绕 reactive 主题，主要的更新点有：

  - 基于 Spring Framework 5（包括新模块：WebFlux）构建
  - 集成 Netty 作为默认的 web 服务器，支持 reactive 应用
  - WebFlux 默认运行在 Netty 上

- Spring Framework 5：New spring-webflux module, an alternative to spring-webmvc built on a reactive foundation, intended for use in an event-loop execution model. 最重要的更新是新增了 WebFlux 模块，支持基于事件循环的执行模型。主要的更新点有：

  - 依赖：最低 Java 8，支持 Java 9
  - 提供许多支持 reactive 的基础设施
  - 提供面向 Netty 等运行时环境的适配器
  - 新增 WebFlux 模块（集成的是 Reactor 3.x）

- Java 9 Reactive Stream：在 Java 8 时代，Reactive Stream API 就已经存在，只不过那时它是单独的一个 jar 包，可用 maven 引入。而在 Java 9 时代，Reactive Stream 被正式集成到了 Java 的 API 中。In Java 9, Reactive Streams is officially part of the Java API。主要的更新点有：

  - 提供 Reactive Stream API（java.util.concurrent.Flow）

Java 9 的 Reactive Stream API 只是一套接口，约定了 Reactive 编程的一套规范，并没有具体的实现。而实现了这个接口的产品有：RxJava、Reactor、akka 等，而 Spring WebFlux 中集成的是 Reactor 3.x。所以目前 Spring Framework 5.x 提供了两大开发栈：

- Spring WebFlux：基于 Reactive Stream API，需要运行在 servlet 3.1+ 容器（Tomcat 8）或 Netty 上，这些容器支持 NIO、Reactor 模式
- Spring MVC：基于传统的 Servlet API，运行在传统的 Servlet 容器上，one-request-per-thread，同步阻塞 IO

下图为两大开发栈的对比：

![两大开发栈对比](https://s3.ax1x.com/2021/02/28/6CgvLT.png?q=a)

Reactive programming 是一种编程范式，它已经存在很久了，并不是什么新的东西。就像 面向过程编程、面向对象编程、函数式编程等，它只是另外一种编程范式。而 Reactive Streams 指的是一套规范，对于 Java 开发者来讲，Reactive Streams 具体来说就是一套 API。Reactive Streams 给我们提供了一套通用的 API，让我们可以使用 Java 进行 Reactive Programming。

# Reactive Stream API

让我们来简要看一下 Reactive Stream API。它只提供了四个接口。

```java
// Publisher：是元素（消息）序列的提供者，根据它的订阅者的需求，来发布这些元素（消息）。
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}

// Subscriber：当通过 Publisher.subscribe(Subscriber) 注册后，它将通过 Subscriber.onSubscribe(Subscription) 来接收消息。
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}

// Subscription：代表了消息从 Publisher 到 Subscriber 的一个一对一的生命周期。
public interface Subscription {
    public void request(long n);
    public void cancel();
}

// Processor：继承了 Publisher 和 Subscriber，用于转换发布者到订阅者之间管道中的元素。Processor<T,R> 订阅类型为 T 的数据元素，接收并转换为类型为 R 的数据，然后发布变换后的数据。
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

![发布者与订阅者之间关系](https://img.imgdb.cn/item/603c88e25f4313ce253d07ab.jpg)

下图显示了发布者和订阅者之间的典型交互顺序。

![发布者与订阅者之间时序交互](https://img.imgdb.cn/item/603c890c5f4313ce253d214f.jpg?q=q)

# 快速开始

## Reactor 框架简介

Reactive Streams API 只是一套接口，并没有具体的实现。Reactor 项目是它的具体实现之一，并且也是 Spring Framework 5 中新增模块 WebFlux 默认集成的框架，Spring Framework 5 的响应式编程模型主要依赖 Reactor。Reactor 有两种模型，Flux 和 Mono，提供了非阻塞、支持回压机制的异步流处理能力。当数据生成缓慢时，整个流自然进入推送模式；而当生产高峰来临数据生产速度加快时，整个流又进入了拉取模式。

- Flux 可以触发 0 到多个事件，用于异步地处理流式的信息；
- Mono 至多可以触发一个事件，通常用于在异步任务完成时发出通知。

Flux 和 Mono 之间可以相互转换。对一个 Flux 序列进行计数操作，得到的结果是一个 Mono 对象，把两个 Mono 序列合并在一起，得到的是一个 Flux 对象。创建 Flux 序列的方式如下：

```java
// 通过 Flux 类的静态方法，例如：
List<String> words = Arrays.asList("aa","bb","cc","dd");
Flux<String> listWords = Flux.fromIterable(words);    //从集合获取
Flux<String> justWords = Flux.just("Hello","World");  //指定序列中包含的全部元素
listWords.subscribe(System.out::println);
justWords.subscribe(System.out::println);

// 使用 generate() 方法生成 Flux 序列： generate() 方法通过同步和逐一的方式来产生 Flux 序列，通过调用 SynchronousSink 对象的 next()，complete() 和 error(Throwable) 方法来完成。
Flux.generate(sink -> {
    sink.next("Hello");   //通过 next()方法产生一个简单的值，至多调用一次
    sink.complete();      //然后通过 complete()方法来结束该序列
}).subscribe(System.out::println);

// 使用 create() 方法生成 Flux 序列：与 generate() 方法的不同之处在于所使用的是 FluxSink 对象。FluxSink 支持同步和异步的消息产生，并且可以在一次调用中产生多个元素。
Flux.create(sink -> {
    for (int i = 0; i < 10; i++) {
        sink.next(i);
    }
    sink.complete();
}).subscribe(System.out::println);
```

创建 Mono 的方式如下：

```java
// Mono 的创建方式与 Flux 类似。除了相同的几个静态方法之外，Mono 还有一些独有的静态方法。例如：
Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
```

流的操作函数如下：

```java
Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);  // 只输出满足filter条件的元素
Flux.just("a", "b").zipWith(Flux.just("c", "d")).subscribe(System.out::println);  // zipWith 操作符把当前流中的元素与另外一个流中的元素按照一对一的方式进行合并。
// reduce 和 reduceWith 操作符对流中包含的所有元素进行累积操作，得到一个包含计算结果的 Mono 序列。
Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
Flux.range(1, 100).reduceWith(() -> 100, (x, y) -> x + y).subscribe(System.out::println);
```

当需要处理 Flux 或 Mono 中的消息时，可以通过 subscribe 方法来添加相应的订阅逻辑。例如：

```java
// 通过 subscribe() 方法处理正常和错误消息：
Flux.just(1, 2)
        .concatWith(Mono.error(new IllegalStateException()))
        .subscribe(System.out::println, System.err::println);

// 出现错误时返回默认值 0：
Flux.just(1, 2)
        .concatWith(Mono.error(new IllegalStateException()))
        .onErrorReturn(0)
        .subscribe(System.out::println);

// 出现错误时使用另外的流：
Flux.just(1, 2)
        .concatWith(Mono.error(new IllegalStateException()))
        .switchOnError(Mono.just(0))
        .subscribe(System.out::println);

// 使用 retry 操作符进行重试：
Flux.just(1, 2)
        .concatWith(Mono.error(new IllegalStateException()))
        .retry(1)
        .subscribe(System.out::println);
```

## WebFlux 模块简介

WebFlux 是 Spring Framework 5 的一个新模块，包含了响应式 HTTP 和 WebSocket 的支持，另外在上层服务端支持两种不同的编程模型：第一种是 Spring MVC 中使用的基于 Java 注解的方式，第二种是基于 Java 8 的 lambda 表达式的函数式编程模型。这两种编程模型只是在代码编写方式上存在不同，它们运行在同样的反应式底层架构之上，因此在运行时是相同的。

![WebFlux 模块](https://img.imgdb.cn/item/603c8b585f4313ce253e29a6.jpg)

```java
// 首先，引入 parent：
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

// 引入 WebFlux 依赖：
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

// 入口文件跟普通的 Spring Boot 项目一样：
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// 创建 RouterFunction 风格的路由：
@Configuration
public class Routes {
    @Bean
    public RouterFunction<?> routerFunction() {
        return route(
            GET("/api/city").and(accept(MediaType.APPLICATION_JSON)), cityService::findAllCity
        ).and(route(
            GET("/api/user/{id}").and(accept(MediaType.APPLICATION_JSON)), cityService::findCityById)
        );
    }
}

// 创建 Spring MVC 注解风格的路由：
@RestController
public class ReactiveController {
    @GetMapping("/hello_world")
    public Mono<String> sayHelloWorld() {
        return Mono.just("Hello World");
    }
}
```

Reactive Controller 操作的是异步的 ServerHttpRequest 和 ServerHttpResponse，而不再是 Spring MVC 里的 HttpServletRequest 和 HttpServletResponse。Mono 和 Flux 是异步的，当流中的数据没有就绪时，方法也能立即返回（返回的是对象引用）。当数据就绪后，web server 会扫描到这个就绪事件。

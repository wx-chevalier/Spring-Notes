# Web Flux

Reactive Programming 在过去早已有之，并不是什么新鲜事物。但是在最近几年，它似乎有着越来越流行的趋势。近期，Java 技术圈围绕着 Reactive Programming 这一主题，推出了许许多多的新版本工具，让人感到眼花缭乱。本文首先选择其中的几项重点更新内容，梳理一下它们之间的关系。

- Spring Boot 2.0：Spring Boot 2.0 are now offering first-class support for developing reactive applications, via auto-configuration and starter-POMs[1]. 一如既往体现着 Spring Boot 配置简便的特点，只需几处简单配置就可以开发 reactive 应用了。围绕 reactive 主题，主要的更新点有：

  - 基于 Spring Framework 5（包括新模块：WebFlux）构建
  - 集成 Netty 作为默认的 web 服务器，支持 reactive 应用
  - WebFlux 默认运行在 Netty 上

- Spring Framework 5：New spring-webflux module, an alternative to spring-webmvc built on a reactive foundation, intended for use in an event-loop execution model[2]. 最重要的更新是新增了 WebFlux 模块，支持基于事件循环的执行模型。主要的更新点有：

  - 依赖：最低 Java 8，支持 Java 9
  - 提供许多支持 reactive 的基础设施
  - 提供面向 Netty 等运行时环境的适配器
  - 新增 WebFlux 模块（集成的是 Reactor 3.x）

- Java 9 Reactive Stream：在 Java 8 时代，Reactive Stream API 就已经存在，只不过那时它是单独的一个 jar 包，可用 maven 引入。而在 Java 9 时代，Reactive Stream 被正式集成到了 Java 的 API 中。In Java 9, Reactive Streams is officially part of the Java API[3]。主要的更新点有：

  - 提供 Reactive Stream API（java.util.concurrent.Flow）

Java 9 的 Reactive Stream API 只是一套接口，约定了 Reactive 编程的一套规范，并没有具体的实现。而实现了这个接口的产品有：RxJava、Reactor、akka 等，而 Spring WebFlux 中集成的是 Reactor 3.x。所以目前 Spring Framework 5.x 提供了两大开发栈：

- Spring WebFlux：基于 Reactive Stream API，需要运行在 servlet 3.1+ 容器（Tomcat 8）或 Netty 上，这些容器支持 NIO、Reactor 模式
- Spring MVC：基于传统的 Servlet API，运行在传统的 Servlet 容器上，one-request-per-thread，同步阻塞 IO

下图为两大开发栈的对比：

![两大开发栈对比](https://s3.ax1x.com/2021/02/28/6CgvLT.png)

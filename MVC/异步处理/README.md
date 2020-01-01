# Spring Boot 中异步请求处理

在 [Linux 并发编程](https://github.com/wx-chevalier/Linux-Series)系列中我们讨论了 Linux 系统中的 IO 模型。

![IO 模型](https://i.postimg.cc/wvr0DwLQ/image.png)

# 同步与异步请求的对比

传统的 Web 应用都会采用同步模式，即浏览器发起请求，Web 服务器开一个线程处理（请求处理线程），处理完把处理结果返回浏览器。

![同步请求的阻塞等待](https://s2.ax1x.com/2020/01/01/lGIfYD.png)

Tomcat 等应用服务器的连接线程池实际上是有限制的；每一个连接请求都会耗掉线程池的一个连接数；如果某些耗时很长的操作，如对大量数据的查询操作、调用外部系统提供的服务以及一些 IO 密集型操作等，会占用连接很长时间，这个时候这个连接就无法被释放而被其它请求重用。如果连接占用过多，服务器就很可能无法及时响应每个请求；极端情况下如果将线程池中的所有连接耗尽，服务器将长时间无法向外提供服务

Spring MVC3.2 之后支持异步请求，能够在 controller 中返回一个 Callable 或者 DeferredResult。

![异步请求非阻塞等待](https://s2.ax1x.com/2020/01/01/lGIxpQ.png)

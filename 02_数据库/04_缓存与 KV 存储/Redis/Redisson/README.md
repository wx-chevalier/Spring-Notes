# Redisson

[Redisson](https://github.com/mrniko/redisson) 是基于 Redis 进行的一种顶层的封装，提供了一系列的分布式与可扩展的 Java 的数据结构。Redisson 底层使用了 Netty 进行封装，同时将 Java 本身大量的类似于 CountDownLatch 这样的同步辅助类利用 Redis 映射到了分布式环境下。在 redisson 中，各个部分均采用了最新的一些技术栈，包括 java 5 线程语义，Promise 编程语义，在技术的学习上有很高的学习意义。相比 jedis，其支持的特性并不是很高，但对于日常的使用还是没有问题的。其对集合的封装，编解码的处理，都达到了一个开箱即用的目的。相比 jedis，仅完成了一个基本的 redis 网络实现，可以理解为 redisson 是一个完整的框架，而 jedis 即完成了语言层的适配。其次，redisson 在设计模式，以及编码上，都有完整的测试示例，代码可读性也非常好，很值得进行源码级学习。如果在项目中已经使用了 netty，那么如果需要集成 redis,那么使用 redisson 是最好的选择了，都不需要另外增加依赖信息。

```java
// connects to default Redis server 127.0.0.1:6379
Redisson redisson = Redisson.create();
// connects to single Redis server via Config
Config config = new Config();
config.useSingleServer().setAddress("127.0.0.1:6379");

//or with database select num = 1
config.useSingleServer().setAddress("127.0.0.1:6379").setDatabase(1);

Redisson redisson = Redisson.create(config);
```

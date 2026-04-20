# Spring Integration

Spring Integration 提供了 Spring 编程模型的扩展用来支持企业集成模式(Enterprise Integration Patterns)，是对 Spring Messaging 的扩展。它提出了不少新的概念，包括消息路由 MessageRoute、消息分发 MessageDispatcher、消息过滤 Filter、消息转换 Transformer、消息聚合 Aggregator、消息分割 Splitter 等等。同时还提供了 MessageChannel 和 MessageHandler 的实现，分别包括 DirectChannel、ExecutorChannel、PublishSubscribeChannel 和 MessageFilter、ServiceActivatingHandler、MethodInvokingSplitter 等内容。

# 消息处理

消息的分割：

![消息的分割](https://s2.ax1x.com/2019/10/19/KnP3wt.png)

消息的聚合：

![消息的聚合](https://s2.ax1x.com/2019/10/19/KnPUSg.png)

消息的过滤：

![消息的过滤](https://s2.ax1x.com/2019/10/19/KnPDwq.png)

消息的分发：

![消息的分发](https://s2.ax1x.com/2019/10/19/KnPrT0.png)

# 简单实例

```java
SubscribableChannel messageChannel =new DirectChannel(); // 1

messageChannel.subscribe(msg-> { // 2
 System.out.println("receive: " +msg.getPayload());
});

messageChannel.send(MessageBuilder.withPayload("msgfrom alibaba").build()); // 3
```

1. 构造一个可订阅的消息通道 `messageChannel`；

2. 使用 `MessageHandler` 去消费这个消息通道里的消息；

3. 发送一条消息到这个消息通道，消息最终被消息通道里的 `MessageHandler` 所消费。

最后控制台打印出: `receive: msg from alibaba`；

`DirectChannel` 内部有个 `UnicastingDispatcher` 类型的消息分发器，会分发到对应的消息通道 `MessageChannel` 中，从名字也可以看出来，`UnicastingDispatcher` 是个单播的分发器，只能选择一个消息通道。那么如何选择呢? 内部提供了 `LoadBalancingStrategy` 负载均衡策略，默认只有轮询的实现，可以进行扩展。

我们对上段代码做一点修改，使用多个 `MessageHandler` 去处理消息：

```java
SubscribableChannel messageChannel = new DirectChannel();

messageChannel.subscribe(msg -> {
     System.out.println("receive1: " + msg.getPayload());
});

messageChannel.subscribe(msg -> {
     System.out.println("receive2: " + msg.getPayload());
});

messageChannel.send(MessageBuilder.withPayload("msg from alibaba").build());
messageChannel.send(MessageBuilder.withPayload("msg from alibaba").build());
```

由于 `DirectChannel` 内部的消息分发器是 `UnicastingDispatcher` 单播的方式，并且采用轮询的负载均衡策略，所以这里两次的消费分别对应这两个 `MessageHandler`。控制台打印出：

```java
receive1: msg from alibaba
receive2: msg from alibaba
```

既然存在单播的消息分发器 `UnicastingDispatcher`，必然也会存在广播的消息分发器，那就是 `BroadcastingDispatcher`，它被 `PublishSubscribeChannel` 这个消息通道所使用。广播消息分发器会把消息分发给所有的 `MessageHandler`：

```java
SubscribableChannel messageChannel = new PublishSubscribeChannel();

messageChannel.subscribe(msg -> {
     System.out.println("receive1: " + msg.getPayload());
});

messageChannel.subscribe(msg -> {
     System.out.println("receive2: " + msg.getPayload());
});

messageChannel.send(MessageBuilder.withPayload("msg from alibaba").build());
messageChannel.send(MessageBuilder.withPayload("msg from alibaba").build());
```

发送两个消息，都被所有的 `MessageHandler` 所消费。控制台打印：

```java
receive1: msg from alibaba
receive2: msg from alibaba
receive1: msg from alibaba
receive2: msg from alibaba
```

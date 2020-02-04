# Spring Messaging

Spring Messaging 是 Spring Framework 中的一个模块，其作用就是统一消息的编程模型。比如消息 Messaging 对应的模型就包括一个消息体 Payload 和消息头 Header:

![消息结构](https://s2.ax1x.com/2019/10/19/KnCV2Q.png)

```java
package org.springframework.messaging;

public interface Message<T> {
  T getPayload();
  MessageHeaders getHeaders();
}
```

消息通道 MessageChannel 用于接收消息，调用 send 方法可以将消息发送至该消息通道中：

![消息传递](https://s2.ax1x.com/2019/10/19/KnCG24.png)

```java
@FunctionalInterface
public interface MessageChannel {
  long INDEFINITE_TIMEOUT = -1;
  default boolean send(Message<?> message) {
    return send(message, INDEFINITE_TIMEOUT);
  }
  boolean send(Message<?> message, long timeout);
}
```

# 消息消费

由消息通道的子接口可订阅的消息通道 SubscribableChannel 实现，被 MessageHandler 消息处理器所订阅:

```java
public interface SubscribableChannel extends MessageChannel {
  boolean subscribe(MessageHandler handler);
  boolean unsubscribe(MessageHandler handler);
}
```

由 MessageHandler 真正地消费/处理消息:

```java
@FunctionalInterface
public interface MessageHandler {
  void handleMessage(Message<?> message) throws MessagingException;
}
```

Spring Messaging 内部在消息模型的基础上衍生出了其它的一些功能，如：

- 消息接收参数及返回值处理：消息接收参数处理器 HandlerMethodArgumentResolver 配合 @Header, @Payload 等注解使用；消息接收后的返回值处理器 HandlerMethodReturnValueHandler 配合 @SendTo 注解使用；

- 消息体内容转换器 MessageConverter；

- 统一抽象的消息发送模板 AbstractMessageSendingTemplate；

- 消息通道拦截器 ChannelInterceptor；

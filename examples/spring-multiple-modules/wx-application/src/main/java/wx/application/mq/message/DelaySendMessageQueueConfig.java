package wx.application.mq.message;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 此处代码用于创建延迟消息队列，使用方案为 TTL + DLX
 *
 * <p>参考文档:https://stackoverflow.com/questions/35227845/scheduled-delay-messaging-in-spring-amqp-rabbitmq</>
 */
@Component
public class DelaySendMessageQueueConfig {

  /** 发送延迟队列 TTL 名称 */
  private static final String SEND_MESSAGE_DELAY_QUEUE = "SEND_MESSAGE_DELAY_QUEUE";

  /** DLX 死信队列的交换机 */
  public static final String SEND_MESSAGE_DELAY_EXCHANGE = "SEND_MESSAGE_DELAY_EXCHANGE";

  /** 死信队列的数据消息发送在该 routingKey 的 */
  public static final String SEND_MESSAGE_DELAY_ROUTING_KEY = "SEND_MESSAGE_DELAY_ROUTING_KEY";

  /** 处理推送消息的队列 */
  static final String SEND_MESSAGE_QUEUE_NAME = "SEND_MESSAGE_QUEUE_NAME";

  /** 处理消息发送的逻辑 */
  private static final String SEND_MESSAGE_EXCHANGE_NAME = "SEND_MESSAGE_EXCHANGE_NAME";

  /** 处理消息发送的路由键 */
  private static final String SEND_MESSAGE_ROUTING_KEY = "SEND_MESSAGE_ROUTING_KEY";

  /**
   * 延迟队列配置
   *
   * <p>延时队列可以在Queue中设置TTL，添加属性 x-message-ttl, 同时可以在Message中设置，这里由于业务需要，已经在msg中设置</>
   *
   * <p>x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称</>
   *
   * <p>x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称</>
   */
  @Bean
  public Queue delayOrderQueue() {
    Map<String, Object> params = new HashMap<>();
    params.put("x-dead-letter-exchange", SEND_MESSAGE_EXCHANGE_NAME);
    params.put("x-dead-letter-routing-key", SEND_MESSAGE_ROUTING_KEY);
    return new Queue(SEND_MESSAGE_DELAY_QUEUE, true, false, false, params);
  }

  /** 定义一个 DirectExchange 交换机 */
  @Bean
  public DirectExchange pushMsgDelayExchange() {
    return new DirectExchange(SEND_MESSAGE_DELAY_EXCHANGE);
  }

  /** 绑定延时队列和交换机 */
  @Bean
  public Binding dlxBinding() {
    return BindingBuilder.bind(delayOrderQueue())
        .to(pushMsgDelayExchange())
        .with(SEND_MESSAGE_DELAY_ROUTING_KEY);
  }

  // 定义处理详细队列
  @Bean
  public Queue orderQueue() {
    return new Queue(SEND_MESSAGE_QUEUE_NAME, true);
  }

  @Bean
  public TopicExchange orderTopicExchange() {
    return new TopicExchange(SEND_MESSAGE_EXCHANGE_NAME);
  }

  @Bean
  public Binding orderBinding() {
    return BindingBuilder.bind(orderQueue())
        .to(orderTopicExchange())
        .with(SEND_MESSAGE_ROUTING_KEY);
  }
}

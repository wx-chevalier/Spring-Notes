package wx.application.infra.message;

import static wx.application.mq.message.DelaySendMessageQueueConfig.SEND_MESSAGE_DELAY_EXCHANGE;
import static wx.application.mq.message.DelaySendMessageQueueConfig.SEND_MESSAGE_DELAY_ROUTING_KEY;
import static wx.common.data.mq.MessageQueueName.SEND_MESSAGE;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wx.application.mq.message.validator.SendMessageValidator;
import wx.application.mq.message.validator.SendMessageValidatorMap;
import wx.common.data.mq.notice.DelaySendNoticeMessage;
import wx.common.data.mq.notice.SendNoticeMessage;

@Slf4j
@Service
public class DelayMessageHandleServiceImpl implements DelayMessageHandleService {

  @Autowired private RabbitTemplate rabbitTemplate;

  // 延时发送消息最大数
  private static final int MAX_SEND_COUNT = 8;

  @Override
  public void handle(DelaySendNoticeMessage msg) {
    // 校验数据
    boolean skip = skip(msg);
    if (skip) {
      log.info("消息不符合发送条件，无法发送");
      return;
    }

    // 获取消息模板信息
    SendMessageValidator validator = SendMessageValidatorMap.get(msg.getNoticeType());
    if (Objects.isNull(validator)) {
      log.error("检查器为空,停止发送");
      return;
    }

    boolean sendAgain = validator.validate(msg.getBaseEntityId());
    if (!sendAgain) {
      log.info("检查发现消息不在需要发送");
      return;
    }
    // 发送通知到通知消息队列
    sendMessageToNoticeMessage(msg);

    // 增加发送次数
    msg.incrementSendCount();
    // 再次发送发送延迟消息
    this.rabbitTemplate.convertAndSend(
        SEND_MESSAGE_DELAY_EXCHANGE,
        SEND_MESSAGE_DELAY_ROUTING_KEY,
        msg,
        message -> {
          message.getMessageProperties().setExpiration(String.valueOf(5 * 1000));
          return message;
        });
    log.info("再次发送延迟消息完成:{}", msg);
  }

  private boolean skip(DelaySendNoticeMessage msg) {
    if (msg.getSendCount() >= MAX_SEND_COUNT) {
      log.info("消息发送达到最大次数:{}", MAX_SEND_COUNT);
      return true;
    }
    if (Objects.isNull(msg.getNoticeType())) {
      log.info("跳过发送延时消息, 消息通知类型模板为空");
      return true;
    }
    return false;
  }

  private void sendMessageToNoticeMessage(DelaySendNoticeMessage msg) {
    SendNoticeMessage noticeMessage = new SendNoticeMessage();
    BeanUtils.copyProperties(msg, noticeMessage);
    this.rabbitTemplate.convertAndSend(SEND_MESSAGE, noticeMessage);
    log.info("发送Msg到消息队列完成:{}", noticeMessage);
  }
}

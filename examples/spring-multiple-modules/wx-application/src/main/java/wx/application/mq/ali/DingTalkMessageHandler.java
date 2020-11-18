package wx.application.mq.ali;

import static wx.common.data.mq.MessageQueueName.DING_TALK_SEND_MESSAGE;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import wx.common.data.mq.ali.DingTalkMessage;
import wx.infra.service.dingtalk.DingTalkService;

@Slf4j
@Component
public class DingTalkMessageHandler {

  private DingTalkService dingTalkService;

  public DingTalkMessageHandler(DingTalkService dingTalkService) {
    this.dingTalkService = dingTalkService;
  }

  @RabbitListener(queuesToDeclare = @Queue(DING_TALK_SEND_MESSAGE))
  public void process(@Valid DingTalkMessage msg) {
    log.info("接收到发送钉钉消息");

    try {
      dingTalkService.sendText(msg.getMessage());
    } catch (Exception e) {
      log.info("发送钉钉消息异常");
      return;
    }

    log.info("钉钉消息发送完成");
  }
}

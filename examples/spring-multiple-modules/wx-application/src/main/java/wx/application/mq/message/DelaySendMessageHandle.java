package wx.application.mq.message;

import static wx.application.mq.message.DelaySendMessageQueueConfig.SEND_MESSAGE_QUEUE_NAME;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import wx.application.infra.message.DelayMessageHandleService;
import wx.application.mq.TaskUtils;
import wx.common.data.mq.notice.DelaySendNoticeMessage;

@Slf4j
@Component
public class DelaySendMessageHandle {

  private TaskUtils taskUtils;

  private DelayMessageHandleService delayMessageHandleService;

  public DelaySendMessageHandle(
      TaskUtils taskUtils, DelayMessageHandleService delayMessageHandleService) {
    this.taskUtils = taskUtils;
    this.delayMessageHandleService = delayMessageHandleService;
  }

  /** 接收到延时消息处理 */
  @RabbitListener(queuesToDeclare = @Queue(SEND_MESSAGE_QUEUE_NAME))
  public void pushMessage(DelaySendNoticeMessage msg) {
    try {
      delayMessageHandleService.handle(msg);
    } catch (Exception e) {
      taskUtils.sendExceptionMarkdownNotice("处理延时消息出现异常", e, msg);
    }
  }
}

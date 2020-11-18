package wx.application.mq.notice;

import static wx.common.data.mq.MessageQueueName.SEND_MESSAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wx.application.infra.message.NoticeMessageHandleService;
import wx.application.mq.TaskUtils;
import wx.common.data.mq.notice.SendNoticeMessage;
import wx.common.data.mq.notice.SendUserMessage;

/** 通知消息处理器 */
@Slf4j
@Component
@RabbitListener(queuesToDeclare = @Queue(SEND_MESSAGE))
public class NoticeMessageHandler {

  private TaskUtils taskUtils;

  private NoticeMessageHandleService noticeMessageHandleService;

  public NoticeMessageHandler(
      TaskUtils taskUtils, NoticeMessageHandleService noticeMessageHandleService) {
    this.taskUtils = taskUtils;
    this.noticeMessageHandleService = noticeMessageHandleService;
  }

  @RabbitHandler
  public void process(SendNoticeMessage msg) {
    try {
      noticeMessageHandleService.handle(msg);
    } catch (Exception e) {
      log.info("发送通知信息出现异常", e);
      taskUtils.sendExceptionMarkdownNotice("处理[SendNoticeMessage]消息出现异常", e, msg);
    }
  }

  @RabbitHandler
  public void process(SendUserMessage msg) {
    try {
      if (StringUtils.hasText(msg.getDest())) {
        noticeMessageHandleService.send(
            msg.getChannel(),
            msg.getDest(),
            msg.getNoticeType(),
            msg.getBaseEntityId(),
            msg.getParam());
      } else if (msg.getUserId() != null) {
        noticeMessageHandleService.send(
            msg.getChannel(),
            msg.getUserId(),
            msg.getNoticeType(),
            msg.getBaseEntityId(),
            msg.getParam());
      }
    } catch (Exception e) {
      taskUtils.sendExceptionMarkdownNotice("处理[SendUserMessage]消息出现异常", e, msg);
      log.info("发送用户信息出现异常", e);
    }
  }
}

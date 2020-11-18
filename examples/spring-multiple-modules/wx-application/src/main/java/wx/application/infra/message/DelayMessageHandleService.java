package wx.application.infra.message;

import javax.validation.constraints.NotNull;
import wx.common.data.mq.notice.DelaySendNoticeMessage;

public interface DelayMessageHandleService {

  /** 处理延时消息 */
  void handle(@NotNull DelaySendNoticeMessage msg);
}

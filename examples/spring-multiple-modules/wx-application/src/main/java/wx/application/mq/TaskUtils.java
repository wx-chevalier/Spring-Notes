package wx.application.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.infra.common.util.ExceptionUtils;
import wx.infra.service.dingtalk.DingTalkService;

@Slf4j
@Component
public class TaskUtils {

  private DingTalkService dingTalkService;

  public TaskUtils(DingTalkService dingTalkService) {
    this.dingTalkService = dingTalkService;
  }

  public void sendExceptionMarkdownNotice(String title, Throwable t, Object... extraNotices) {
    StringBuilder content = new StringBuilder("\n```\n" + ExceptionUtils.getStackTrace(t) + "```");
    for (Object notices : extraNotices) {
      content.append("\n\n").append(notices);
    }

    try {
      dingTalkService.sendMarkdown(title, content.toString());
    } catch (Throwable err) {
      log.error("Error sending err msg: {}", title);
    }
  }

  public void send(String content, Object extraNotices) {
    String format = String.format("wx: %s 参数: %s", content, String.valueOf(extraNotices));
    try {
      dingTalkService.sendText(format);
    } catch (Throwable err) {
      log.error("Error sending err msg: {}", format);
    }
  }
}

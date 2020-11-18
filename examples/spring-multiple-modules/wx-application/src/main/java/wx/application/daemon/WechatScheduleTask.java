package wx.application.daemon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wx.application.wechat.WechatCommandService;

/** 微信定时任务 */
@Slf4j
@Component
public class WechatScheduleTask {

  private WechatCommandService wechatCommandService;

  public WechatScheduleTask(WechatCommandService wechatCommandService) {
    this.wechatCommandService = wechatCommandService;
  }

  /**
   * 微信公众号更新AccessToken定时任务
   *
   * <p>每隔 5000 秒执行一次的
   */
  @Scheduled(fixedDelay = 5000_000, initialDelay = 2000_000)
  public void updateAccessTokenTask() {
    try {
      wechatCommandService.updateAccessToken();
      log.info("定时更新微信AccessToken完成");
    } catch (Exception e) {
      log.error("定时更新微信AccessToken失败,{}", e.getMessage());
    }
  }
}

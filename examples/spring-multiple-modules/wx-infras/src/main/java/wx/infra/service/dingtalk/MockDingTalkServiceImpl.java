package wx.infra.service.dingtalk;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnMissingBean(DingTalkSetting.class)
public class MockDingTalkServiceImpl implements DingTalkService {

  @Override
  public DingTalkSetting getDingTalkSetting() {
    return new DingTalkSetting("MOCK DING TALK TOKEN");
  }

  @Override
  public void sendText(String textMessage, String... atMobiles) throws DingTalkServiceException {
    for (String atMobile : atMobiles) {
      log.warn("DING: @{} {}", atMobile, textMessage);
    }
  }

  @Override
  public void sendLink(String title, String messageUrl, String text, @Nullable String pictureUrl)
      throws DingTalkServiceException {
    log.warn("DING: title={} link={} picture={}", title, messageUrl, pictureUrl);
  }

  @Override
  public void sendMarkdown(String title, String markdownText) throws DingTalkServiceException {
    log.warn("DING: title={} markdown={}", title, markdownText);
  }
}

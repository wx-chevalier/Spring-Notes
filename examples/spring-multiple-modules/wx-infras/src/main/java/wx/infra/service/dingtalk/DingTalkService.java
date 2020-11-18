package wx.infra.service.dingtalk;

import org.jetbrains.annotations.Nullable;

public interface DingTalkService {

  DingTalkSetting getDingTalkSetting();

  void sendText(String textMessage, String... atMobiles) throws DingTalkServiceException;

  void sendLink(String title, String messageUrl, String text, @Nullable String pictureUrl)
      throws DingTalkServiceException;

  void sendMarkdown(String title, String markdownText) throws DingTalkServiceException;
}

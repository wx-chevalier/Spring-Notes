package wx.infra.service.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(DingTalkSetting.class)
public class DingTalkServiceImpl implements DingTalkService {

  @Getter private DingTalkSetting dingTalkSetting;

  private DingTalkClient dingTalkClient;

  public DingTalkServiceImpl(DingTalkSetting dingTalkSetting) {
    this.dingTalkSetting = dingTalkSetting;
    String serverUrl =
        String.format(
            "https://oapi.dingtalk.com/robot/send?access_token=%s",
            dingTalkSetting.getAccessToken());
    this.dingTalkClient = new DefaultDingTalkClient(serverUrl);
  }

  @Override
  public void sendText(String textMessage, String... atMobiles) {
    log.info("Sending text: {} {}", textMessage, atMobiles);
    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("text");
    OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
    text.setContent(textMessage);
    request.setText(text);
    if (atMobiles.length != 0) {
      OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
      at.setAtMobiles(Arrays.asList(atMobiles));
      request.setAt(at);
    }
    executeRequest(request);
  }

  @Override
  public void sendLink(String title, String messageUrl, String text, @Nullable String pictureUrl) {
    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("link");
    OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
    link.setPicUrl(pictureUrl);
    link.setMessageUrl(messageUrl);
    link.setTitle(title);
    link.setText(text);
    request.setLink(link);
    executeRequest(request);
  }

  public void sendMarkdown(String title, String markdownText) {
    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("markdown");
    OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
    markdown.setTitle(title);
    markdown.setText(markdownText);
    request.setMarkdown(markdown);
    executeRequest(request);
  }

  private void executeRequest(OapiRobotSendRequest request) {
    OapiRobotSendResponse response;
    try {
      response = dingTalkClient.execute(request);
    } catch (ApiException e) {
      throw new DingTalkServiceException("ApiException", e);
    } catch (Throwable t) {
      throw new DingTalkServiceException(t);
    }
    if (response.getErrcode() != 0) {
      throw new DingTalkServiceException(response.getErrorCode() + ": " + response.getErrmsg());
    }
  }
}

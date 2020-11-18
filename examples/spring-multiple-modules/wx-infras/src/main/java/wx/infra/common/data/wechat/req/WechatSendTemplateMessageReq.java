package wx.infra.common.data.wechat.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class WechatSendTemplateMessageReq {

  private String touser;

  @JsonProperty("template_id")
  private String templateId;

  private Map<String, WechatTemplateMessageValue> data;

  @Data
  public static class WechatTemplateMessageValue {

    private String value;

    private String color = "#000000";

    public static WechatTemplateMessageValue create(String value) {
      return new WechatTemplateMessageValue().setValue(value);
    }

    public static WechatTemplateMessageValue create(String value, String colorValue) {
      return new WechatTemplateMessageValue().setValue(value).setColor(colorValue);
    }
  }
}

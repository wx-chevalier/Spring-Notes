package wx.infra.common.data.wechat.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WechatQrCodeReq {

  @JsonProperty("expire_seconds")
  private Long expireSeconds = 12000L;

  @JsonProperty("action_name")
  private String actionName = "QR_STR_SCENE";

  @JsonProperty("action_info")
  private WechatActionInfo actionInfo;

  public WechatQrCodeReq(WechatActionInfo actionInfo) {
    this.actionInfo = actionInfo;
  }
}

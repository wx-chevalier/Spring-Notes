package wx.infra.common.data.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WechatSendMessageResp {

  @JsonProperty("errorcode")
  private Long errorCode;

  @JsonProperty("errmsg")
  private String errMsg;

  @JsonProperty("msgid")
  private Long msgId;
}

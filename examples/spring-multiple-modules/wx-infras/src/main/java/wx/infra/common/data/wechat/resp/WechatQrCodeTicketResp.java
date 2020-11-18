package wx.infra.common.data.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WechatQrCodeTicketResp {

  private static final String QR_CODE_IMG_URL_PREFIX =
      "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";

  @JsonProperty("errorcode")
  private Long errorCode;

  private String ticket;

  @JsonProperty("expire_seconds")
  private String expireSeconds;

  private String url;

  public String getQrCodeImgUrl() {
    return QR_CODE_IMG_URL_PREFIX + ticket;
  }
}

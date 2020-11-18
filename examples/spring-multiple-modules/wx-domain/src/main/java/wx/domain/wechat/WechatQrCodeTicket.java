package wx.domain.wechat;

import lombok.Data;

@Data
public class WechatQrCodeTicket {

  private String ticket;

  private String expireSeconds;

  private String url;

  public String qrCodeImgUrl;
}

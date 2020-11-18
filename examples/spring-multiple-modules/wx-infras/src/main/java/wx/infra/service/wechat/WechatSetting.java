package wx.infra.service.wechat;

import lombok.Data;

@Data
public class WechatSetting {

  private String appID;

  private String appSecret;

  private String token;
}

package wx.api.config.properties;

import lombok.Data;

/** 微信配置 */
@Data
public class WechatProperties {
  private String appID;

  private String appSecret;

  private String token;
}

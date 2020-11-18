package wx.api.config.properties;

import lombok.Data;

/** 阿里云OSS服务配置 */
@Data
public class SMSProperties {

  private String regionId = "cn-hangzhou";

  private String signName;

  private String accessKeyId;

  private String accessKeySecret;
}

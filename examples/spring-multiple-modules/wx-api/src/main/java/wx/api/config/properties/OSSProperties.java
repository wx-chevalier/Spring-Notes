package wx.api.config.properties;

import lombok.Data;

/** 阿里云OSS服务配置 */
@Data
public class OSSProperties {

  private String accessKeyId;

  private String accessKeySecret;

  private String endpoint;

  private String bucketName;

  private Long expiration;

  private Long maxSize;

  private String callbackUrl;
}

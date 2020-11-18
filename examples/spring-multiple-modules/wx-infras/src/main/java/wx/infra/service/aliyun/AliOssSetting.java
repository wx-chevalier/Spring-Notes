package wx.infra.service.aliyun;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AliOssSetting {

  private String accessKeyId;

  private String accessKeySecret;

  private String endpoint;

  private String bucketName;

  // 秒
  private Long expiration;

  private Long maxSize;

  private String callbackUrl;

  public AliOssSetting(
      String accessKeyId,
      String accessKeySecret,
      String endpoint,
      String bucketName,
      Long expiration,
      Long maxSize,
      String callbackUrl) {
    this.accessKeyId = accessKeyId;
    this.accessKeySecret = accessKeySecret;
    this.endpoint = endpoint;
    this.bucketName = bucketName;
    this.expiration = expiration;
    this.maxSize = maxSize;
    this.callbackUrl = callbackUrl;
  }

  public String getAction() {
    return String.format("https://%s.%s", getBucketName(), getEndpoint());
  }
}

package wx.infra.service.aliyun.model;

import lombok.Data;

@Data
public class AliOssAuth {

  private String accessKeyId;

  private String signature;

  private String action;

  private String dir;

  private String policy;

  public AliOssAuth(
      String accessKeyId, String signature, String action, String dir, String policy) {
    this.accessKeyId = accessKeyId;
    this.signature = signature;
    this.action = action;
    this.dir = dir;
    this.policy = policy;
  }
}

package wx.infra.service.sms;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AliSmsSetting {

  private String regionId;

  private String signName;

  private String accessKeyId;

  private String accessSecret;

  public AliSmsSetting(String regionId, String signName, String accessKeyId, String accessSecret) {
    this.regionId = regionId;
    this.signName = signName;
    this.accessKeyId = accessKeyId;
    this.accessSecret = accessSecret;
  }
}

package wx.infra.common.data.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WechatAccessTokenResp {

  @JsonProperty("errorcode")
  private Long errorCode;

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expires_in")
  private Integer expiresIn;

  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;
}

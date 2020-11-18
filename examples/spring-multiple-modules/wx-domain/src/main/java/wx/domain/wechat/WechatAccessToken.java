package wx.domain.wechat;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WechatAccessToken {

  private Long id;

  private String accessToken;

  private Integer expiresIn;

  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;
}

package wx.infra.tunnel.db.infra.wechat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("infra_wechat_token")
public class WechatAccessTokenDO {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String accessToken;

  private Integer expiresIn;

  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;
}

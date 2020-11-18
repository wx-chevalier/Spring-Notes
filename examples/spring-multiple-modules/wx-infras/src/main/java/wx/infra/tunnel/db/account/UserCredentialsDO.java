package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("u_user_credentials")
public class UserCredentialsDO extends BaseDO<UserCredentialsDO> {

  private static final long serialVersionUID = 1L;

  private Long userId;

  private String activateToken;

  private Boolean isEnabled;

  private String password;

  private String resetToken;
}

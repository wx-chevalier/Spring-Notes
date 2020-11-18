package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.account.Authority;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("u_user")
public class UserDO extends BaseDO<UserDO> {

  private String username;

  private Authority authority;

  private String phoneNumber;

  private String nickName;

  private String email;

  private Long avatarFileId;

  private String remark;

  private Long creatorId;
}

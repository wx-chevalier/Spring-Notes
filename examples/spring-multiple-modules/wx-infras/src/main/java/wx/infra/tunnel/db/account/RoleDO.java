package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.account.RoleStatusEnum;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("u_role")
public class RoleDO extends BaseDO<RoleDO> {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  private String name;

  private String nickname;

  private String description;

  private RoleStatusEnum status;

  private Long creatorId;
}

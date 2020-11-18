package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.account.PermissionStatusEnum;
import wx.common.data.account.PermissionTypeEnum;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("u_permission")
public class PermissionDO extends BaseDO<PermissionDO> {

  private static final long serialVersionUID = 1L;

  /** 权限昵称(中文名称) */
  private String nickname;

  /** 权限名称 */
  private String name;

  /** 权限图标 */
  private String icon;

  /** 权限类型 权限类型：0->目录；1->菜单；2->按钮（接口绑定权限） */
  private PermissionTypeEnum type;

  /** 权限描述内容 */
  private String description;

  /** 权限状态 启用状态；0->禁用；1->启用 */
  private PermissionStatusEnum status;

  private Long creatorId;
}

package wx.infra.tunnel.db.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_permission_setting")
public class PermissionSettingDO extends BaseDO<PermissionSettingDO> {

  private String permissionName;

  private long applicationId;

  private String directory;
}

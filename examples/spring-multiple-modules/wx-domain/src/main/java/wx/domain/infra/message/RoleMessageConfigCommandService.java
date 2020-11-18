package wx.domain.infra.message;

import java.util.List;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;

public interface RoleMessageConfigCommandService {

  /** 更新指定角色的配置的信息 */
  void update(TenantId tenantId, UserId userId, RoleId roleId, List<RoleMessageConfig> configs);
}

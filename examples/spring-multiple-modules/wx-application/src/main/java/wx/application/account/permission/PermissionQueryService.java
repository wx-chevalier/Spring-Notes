package wx.application.account.permission;

import java.util.List;
import java.util.Set;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;

public interface PermissionQueryService {
  List<PermissionDetail> findByRoleIds(TenantId tenantId, Set<RoleId> roleIds);
}

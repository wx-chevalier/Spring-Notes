package wx.application.account.role;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Role;

public interface RoleQueryService {

  /** 通过用户ID查询角色列表 */
  Single<List<Role>> findRoleByUserId(TenantId tenantId, UserId userId);
}

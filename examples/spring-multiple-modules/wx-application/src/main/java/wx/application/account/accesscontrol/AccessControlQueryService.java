package wx.application.account.accesscontrol;

import java.util.List;
import wx.application.account.role.RoleDetail;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Permission;
import wx.domain.account.Role;

public interface AccessControlQueryService {

  List<RoleDetail> findRoles(TenantId tenantId);

  List<Role> findUserRoles(TenantId tenantId, UserId userId);

  /**
   * 获取用户权限
   *
   * <ul>
   *   <li>用户的角色权限
   *   <li>对用户直接设定的权限，可能禁用掉来自用户角色的权限
   * </ul>
   */
  List<Permission> findUserPermissions(TenantId tenantId, UserId userId);

  List<Permission> findRolePermissions(TenantId tenantId, Role role);
}

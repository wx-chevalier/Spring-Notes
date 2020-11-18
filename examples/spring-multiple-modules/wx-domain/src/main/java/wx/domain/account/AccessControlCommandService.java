package wx.domain.account;

import java.util.Collection;
import java.util.List;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;

public interface AccessControlCommandService {

  Role addRole(Role role);

  Permission addPermission(TenantId tenantId, Permission permission);

  boolean addRolePermission(TenantId tenantId, UserId operatorId, Role role, Permission permission);

  boolean addRolePermissions(
      TenantId tenantId, UserId operatorId, Role role, List<Permission> permissions);

  boolean removeRolePermission(TenantId tenantId, Role role, Permission permission);

  boolean removeRolePermissions(TenantId tenantId, Role role, List<Permission> permissions);

  boolean addUserRole(TenantId tenantId, UserId userId, Role role);

  boolean addUserRole(
      TenantId tenantId, UserId userId, Collection<RoleId> roleIds, UserId creatorId);

  boolean removeUserRole(TenantId tenantId, UserId userId, Role role);

  boolean addUserPermission(TenantId tenantId, UserId userId, Permission permission);

  boolean removeUserPermission(TenantId tenantId, UserId userId, Permission permission);

  /** 删除某个租户{@param tenantId}下的角色{@param roleName} */
  void removeRole(TenantId tenantId, String roleName);
}

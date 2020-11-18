package wx.domain.account;

import java.util.List;
import java.util.Optional;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;

public interface RoleRepository {

  Role saveOrUpdate(Role role);

  boolean remove(TenantId tenantId, String name);

  Optional<Role> findByName(TenantId tenantId, String name);

  List<Role> findRoles(TenantId tenantId);

  /** 判断指定租户下存在某个角色 */
  boolean exist(TenantId tenantId, RoleId roleId);
}

package wx.domain.account;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import wx.common.data.shared.id.*;

public interface PermissionRepository {

  Permission saveOrUpdate(TenantId tenantId, Permission role);

  boolean remove(TenantId tenantId, String name);

  Optional<Permission> findByName(TenantId tenantId, String name);

  List<Permission> findPermissions(TenantId tenantId);

  /** 通过权限名称获取权限的nickName */
  Map<String, Permission> findByNames(Set<String> permissionNameSet);
}

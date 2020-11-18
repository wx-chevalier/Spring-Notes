package wx.domain.account;

import java.util.List;
import java.util.Optional;
import wx.common.data.shared.id.*;
import wx.domain.shared.IdBasedEntityRepository;

public interface TenantRepository extends IdBasedEntityRepository<TenantId, Tenant> {

  default Optional<Tenant> findById(TenantId tenantId) {
    return findById(TenantId.NULL_TENANT_ID, tenantId);
  }

  default boolean exists(TenantId tenantId) {
    return exists(TenantId.NULL_TENANT_ID, tenantId);
  }

  default Tenant save(Tenant tenant) {
    return save(TenantId.NULL_TENANT_ID, tenant);
  }

  default boolean removeById(TenantId tenantId) {
    return removeById(TenantId.NULL_TENANT_ID, tenantId);
  }

  List<Tenant> findAll();

  /** 通过租户名获取租户信息 */
  boolean exists(String name);
}

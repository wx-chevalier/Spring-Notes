package wx.application.account.user;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.CompanyId;
import wx.domain.account.Tenant;
import wx.infra.tunnel.db.account.TenantDO;

@Component
public class TenantConverter extends AbstractConverter<Tenant, TenantDO> {

  @Override
  public TenantDO convertTo(Tenant tenant) {
    return new TenantDO()
        .setName(tenant.getName())
        .setAreaCode(tenant.getAreaCode())
        .setCompanyId(convertNullable(tenant.getCompanyId(), BaseEntityId::getId))
        .setTenantId(TenantId.NULL_TENANT_ID.getId());
  }

  @Override
  public Tenant convertFrom(TenantDO tenantDO) {
    return new Tenant(
        new TenantId(tenantDO.getId()),
        TenantId.create(tenantDO.getTenantId()),
        tenantDO.getCreatedAt(),
        tenantDO.getUpdatedAt(),
        tenantDO.getName(),
        new CompanyId(tenantDO.getCompanyId()),
        tenantDO.getAreaCode());
  }
}

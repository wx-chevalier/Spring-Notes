package wx.domain.account;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.CompanyId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tenant extends IdBasedEntity<TenantId, Tenant> {

  private String name;

  private String code;

  private String areaCode;

  private CompanyId companyId;

  public Tenant(
      TenantId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String name,
      CompanyId companyId,
      String areaCode) {
    super(id, tenantId, createdAt, updatedAt);
    this.name = name;
    this.companyId = companyId;
    this.areaCode = areaCode;
  }

  public Tenant(String name, String areaCode) {
    super(TenantId.NULL_TENANT_ID);
    this.name = name;
    this.areaCode = areaCode;
  }
}

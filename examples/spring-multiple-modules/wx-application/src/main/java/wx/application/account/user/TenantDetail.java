package wx.application.account.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import wx.domain.account.Tenant;
import wx.domain.account.User;
import wx.domain.infra.area.Area;

@Data
@EqualsAndHashCode(callSuper = true)
public class TenantDetail extends Tenant {

  private User tenantAdminUser;

  private Area area;

  private Integer utkPrinterCount;

  public TenantDetail(Tenant tenant, User tenantAdminUser, Area area, Integer utkPrinterCount) {
    BeanUtils.copyProperties(tenant, this);
    this.tenantAdminUser = tenantAdminUser;
    this.area = area;
    this.utkPrinterCount = utkPrinterCount;
  }
}

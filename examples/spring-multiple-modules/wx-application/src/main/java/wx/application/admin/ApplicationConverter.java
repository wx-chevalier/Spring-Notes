package wx.application.admin;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.domain.admin.Application;
import wx.infra.tunnel.db.admin.ApplicationDO;

@Component
public class ApplicationConverter extends AbstractConverter<Application, ApplicationDO> {

  @Override
  public ApplicationDO convertTo(Application application) {
    return new ApplicationDO()
        .setId(application.getId().getId())
        .setTenantId(application.getTenantId().getId())
        .setCreatedAt(application.getCreatedAt())
        .setUpdatedAt(application.getUpdatedAt())
        .setName(application.getName());
  }

  @Override
  public Application convertFrom(ApplicationDO applicationDO) {
    return new Application(
        new ApplicationId(applicationDO.getId()),
        new TenantId(applicationDO.getTenantId()),
        applicationDO.getCreatedAt(),
        applicationDO.getUpdatedAt(),
        applicationDO.getName());
  }
}

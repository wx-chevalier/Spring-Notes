package wx.application.admin;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.shared.id.*;
import wx.domain.admin.Application;
import wx.infra.tunnel.db.admin.ApplicationTunnel;

@Service
public class ApplicationQueryServiceImpl implements ApplicationQueryService {

  private ApplicationTunnel applicationTunnel;

  private ApplicationConverter applicationConverter;

  public ApplicationQueryServiceImpl(
      ApplicationTunnel applicationTunnel, ApplicationConverter applicationConverter) {
    this.applicationTunnel = applicationTunnel;
    this.applicationConverter = applicationConverter;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Application> getByTenantId(TenantId tenantId) {
    // TODO 获取租户授权的应用列表
    return applicationTunnel.getAll().stream()
        .map(applicationConverter::convertFrom)
        .collect(Collectors.toList());
  }
}

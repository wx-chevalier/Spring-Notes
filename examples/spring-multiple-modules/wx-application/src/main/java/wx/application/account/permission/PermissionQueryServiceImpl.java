package wx.application.account.permission;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.shared.id.*;
import wx.domain.account.Permission;
import wx.domain.admin.Application;
import wx.domain.admin.ApplicationRepository;
import wx.infra.tunnel.db.account.PermissionDO;
import wx.infra.tunnel.db.account.RolePermissionRelationTunnel;

@Slf4j
@Service
public class PermissionQueryServiceImpl implements PermissionQueryService {

  private RolePermissionRelationTunnel rolePermissionRelationTunnel;

  private PermissionConverter permissionConverter;

  private ApplicationRepository applicationRepository;

  public PermissionQueryServiceImpl(
      RolePermissionRelationTunnel rolePermissionRelationTunnel,
      PermissionConverter permissionConverter,
      ApplicationRepository applicationRepository) {
    this.rolePermissionRelationTunnel = rolePermissionRelationTunnel;
    this.permissionConverter = permissionConverter;
    this.applicationRepository = applicationRepository;
  }

  @Override
  @Transactional
  public List<PermissionDetail> findByRoleIds(TenantId tenantId, Set<RoleId> roleIds) {

    // 获取应用信息
    Map<ApplicationId, Application> applicationGroup =
        applicationRepository.find(TenantId.NULL_TENANT_ID).stream()
            .collect(Collectors.toMap(Application::getId, Function.identity()));

    return rolePermissionRelationTunnel.findByRoleIds(tenantId, roleIds).stream()
        .map(
            appPermissionDO -> {
              Application application =
                  applicationGroup.get(new ApplicationId(appPermissionDO.getApplicationId()));

              PermissionDO permissionDO = new PermissionDO();
              BeanUtils.copyProperties(appPermissionDO, permissionDO);
              Permission permission = permissionConverter.convertFrom(permissionDO);

              return new PermissionDetail(permission, application, appPermissionDO.getDirectory());
            })
        .collect(Collectors.toList());
  }
}

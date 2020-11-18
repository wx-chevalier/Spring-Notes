package wx.application.admin;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.PermissionSettingId;
import wx.domain.admin.PermissionSetting;
import wx.domain.admin.PermissionSettingRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.admin.PermissionSettingDO;
import wx.infra.tunnel.db.admin.PermissionSettingTunnel;
import wx.infra.tunnel.db.mapper.admin.PermissionSettingMapper;

@Repository
public class PermissionSettingRepositoryImpl
    extends MyBatisIdBasedEntityRepository<
        PermissionSettingTunnel,
        PermissionSettingMapper,
        PermissionSettingDO,
        PermissionSetting,
        PermissionSettingId>
    implements PermissionSettingRepository {

  @Getter(AccessLevel.PROTECTED)
  private PermissionSettingConverter converter;

  public PermissionSettingRepositoryImpl(
      PermissionSettingTunnel permissionSettingTunnel,
      PermissionSettingMapper mapper,
      PermissionSettingConverter converter) {
    super(permissionSettingTunnel, mapper);
    this.converter = converter;
  }

  @Override
  public List<PermissionSetting> findByAppId(TenantId tenantId, ApplicationId appId) {
    List<PermissionSettingDO> permissionSettingDOS = getTunnel().list(tenantId, appId);
    return permissionSettingDOS.stream().map(converter::convertFrom).collect(Collectors.toList());
  }
}

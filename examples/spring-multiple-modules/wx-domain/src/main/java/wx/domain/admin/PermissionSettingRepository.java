package wx.domain.admin;

import java.util.List;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.PermissionSettingId;
import wx.domain.shared.IdBasedEntityRepository;

public interface PermissionSettingRepository
    extends IdBasedEntityRepository<PermissionSettingId, PermissionSetting> {

  /** 获取某个租户下某个应用列表 */
  List<PermissionSetting> findByAppId(TenantId tenantId, ApplicationId appId);
}

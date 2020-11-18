package wx.application.admin;

import java.util.List;
import wx.common.data.shared.id.*;
import wx.domain.admin.Application;

public interface ApplicationQueryService {

  /** 获取指定租户的可用的应用列表 */
  List<Application> getByTenantId(TenantId tenantId);
}

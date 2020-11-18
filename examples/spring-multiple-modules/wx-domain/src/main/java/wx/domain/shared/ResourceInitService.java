package wx.domain.shared;

import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;

public interface ResourceInitService {

  /** 为租户{@param tenantId} 初始化资源 */
  void init(TenantId tenantId, UserId tenantAdminUserId);
}

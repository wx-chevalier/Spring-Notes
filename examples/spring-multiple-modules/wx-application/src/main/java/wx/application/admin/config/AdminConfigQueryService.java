package wx.application.admin.config;

import wx.common.data.shared.id.*;

public interface AdminConfigQueryService {
  /** 获取某个租户的材料配置 */
  FileTemplateConfig getTemplateConfig(TenantId tenantId);
}

package wx.api.config.security.model;

import lombok.Data;
import wx.common.data.account.Authority;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.UserId;

@Data
public class SecurityUser {
  private UserId id;

  private TenantId tenantId;

  private Authority authority;

  private String username;

  // access key
  private String accessKey;

  private EntityId entityId;
}

package wx.api.rest.auth;

import lombok.Data;
import wx.common.data.shared.EntityType;

@Data
public class AccessKeyCreateRequest {

  private String entityId;

  private EntityType entityType;
}

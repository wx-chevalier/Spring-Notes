package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class RoleMessageConfigId extends BaseEntityId {

  public RoleMessageConfigId(Long id) {
    super(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.ROLE_MESSAGE_CONFIG_ID;
  }
}

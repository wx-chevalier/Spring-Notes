package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class PermissionSettingId extends BaseEntityId {

  public PermissionSettingId(Long id) {
    super(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.PERMISSION_SETTING;
  }
}

package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class RoleId extends BaseEntityId {

  public RoleId(String id) {
    super(Long.parseUnsignedLong(id));
  }

  public RoleId(Long id) {
    super(id);
  }

  public static RoleId create(String id) {
    if (id == null || id.length() == 0) {
      return null;
    }
    return new RoleId(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.ROLE;
  }
}

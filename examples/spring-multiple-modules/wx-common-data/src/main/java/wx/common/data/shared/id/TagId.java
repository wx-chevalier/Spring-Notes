package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class TagId extends BaseEntityId {

  public TagId(Long id) {
    super(id);
  }

  public TagId(String id) {
    super(Long.parseUnsignedLong(id));
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.TAG;
  }
}

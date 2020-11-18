package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class MessageTypeId extends BaseEntityId {

  public MessageTypeId(Long id) {
    super(id);
  }

  public MessageTypeId(String id) {
    super(Long.valueOf(id));
  }

  public static MessageTypeId create(String id) {
    if (id == null || id.length() == 0) {
      return null;
    }
    return new MessageTypeId(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.MESSAGE_TYPE_ID;
  }
}

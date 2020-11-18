package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class MessageNoticeId extends BaseEntityId {
  public MessageNoticeId(Long id) {
    super(id);
  }

  public MessageNoticeId(String messageNoticeId) {
    super(Long.parseUnsignedLong(messageNoticeId));
  }

  public static MessageNoticeId create(String messageNoticeId) {
    if (messageNoticeId == null || messageNoticeId.length() == 0) {
      return null;
    }
    return new MessageNoticeId(messageNoticeId);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.SITE_MESSAGE_ID;
  }
}

package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class FileId extends BaseEntityId {

  public FileId(Long id) {
    super(id);
  }

  public FileId(String id) {
    super(Long.parseUnsignedLong(id));
  }

  public static FileId create(String id) {
    if (id == null || id.length() == 0) {
      return null;
    }
    return new FileId(Long.parseUnsignedLong(id));
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.FILE;
  }
}

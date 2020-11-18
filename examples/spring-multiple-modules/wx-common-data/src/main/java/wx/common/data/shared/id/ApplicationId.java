package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class ApplicationId extends BaseEntityId {

  public ApplicationId(String id) {
    super(Long.valueOf(id));
  }

  public ApplicationId(Long id) {
    super(id);
  }

  public static ApplicationId create(String id) {
    if (id == null || id.length() == 0) {
      return null;
    }
    return new ApplicationId(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.APPLICATION;
  }
}

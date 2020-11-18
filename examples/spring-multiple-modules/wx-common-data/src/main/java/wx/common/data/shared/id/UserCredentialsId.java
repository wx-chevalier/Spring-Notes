package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class UserCredentialsId extends BaseEntityId {

  public UserCredentialsId(Long id) {
    super(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.USER_CREDENTIALS;
  }
}

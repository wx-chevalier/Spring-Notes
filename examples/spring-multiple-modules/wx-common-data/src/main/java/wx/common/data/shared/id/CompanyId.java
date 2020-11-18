package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class CompanyId extends BaseEntityId {

  public CompanyId(Long id) {
    super(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.COMPANY;
  }
}

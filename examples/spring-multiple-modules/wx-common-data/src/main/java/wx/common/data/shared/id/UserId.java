package wx.common.data.shared.id;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import wx.common.data.shared.EntityType;

public class UserId extends BaseEntityId {

  public static final UserId NULL_USER_ID = new UserId(0L);

  public UserId(Long id) {
    super(id);
  }

  public static UserId create(@Nullable Long id) {
    if (id == null || Objects.equals(id, NULL_USER_ID.getId())) {
      return NULL_USER_ID;
    } else {
      return new UserId(id);
    }
  }

  public static UserId create(String id) {
    return new UserId(Long.valueOf(id));
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.USER;
  }
}

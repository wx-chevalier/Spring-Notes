package wx.common.data.shared.id;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import wx.common.data.shared.EntityType;

public class TenantId extends BaseEntityId {

  public static final TenantId NULL_TENANT_ID = new TenantId(0L);

  public TenantId(Long id) {
    super(id);
  }

  public TenantId(String id) {
    super(Long.parseUnsignedLong(id));
  }

  public static TenantId create(@Nullable Long id) {
    if (id == null || Objects.equals(id, NULL_TENANT_ID.getId())) {
      return NULL_TENANT_ID;
    } else {
      return new TenantId(id);
    }
  }

  public static TenantId create(@Nullable String id) {
    if (id == null || id.length() == 0) {
      return null;
    }
    return new TenantId(id);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.TENANT;
  }
}

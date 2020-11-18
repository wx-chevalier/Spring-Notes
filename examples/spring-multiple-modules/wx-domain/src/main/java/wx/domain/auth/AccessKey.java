package wx.domain.auth;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import wx.common.data.shared.EntityType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.EntityIdFactory;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessKey {

  private TenantId tenantId;

  private String key;

  private String secret;

  private EntityId entityId;

  public static AccessKey assemble(
      Long tenantId, String key, String secret, Long entityId, EntityType entityType) {
    return new AccessKey()
        .setTenantId(new TenantId(tenantId))
        .setKey(key)
        .setSecret(secret)
        .setEntityId(EntityIdFactory.getByTypeAndId(entityType, entityId));
  }

  public static AccessKey create(TenantId tenantId, Long entityId, EntityType entityType) {
    return new AccessKey()
        .setTenantId(tenantId)
        .setKey(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 24))
        .setSecret(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 30))
        .setEntityId(EntityIdFactory.getByTypeAndId(entityType, entityId));
  }
}

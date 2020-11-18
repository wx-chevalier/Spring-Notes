package wx.common.data.shared.id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import wx.common.data.shared.EntityType;
import wx.common.data.shared.HasId;

@JsonDeserialize(using = EntityIdDeserializer.class)
@JsonSerialize(using = EntityIdSerializer.class)
public interface EntityId extends HasId<Long>, Serializable {
  EntityType getEntityType();

  static String getIdString(BaseEntityId entityId) {
    return Long.toUnsignedString(entityId.getId());
  }
}

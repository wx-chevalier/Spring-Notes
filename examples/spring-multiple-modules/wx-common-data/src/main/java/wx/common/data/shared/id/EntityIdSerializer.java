package wx.common.data.shared.id;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class EntityIdSerializer extends JsonSerializer<EntityId> {
  @Override
  public void serialize(EntityId value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    // TODO: 暂时所有的 ID 被序列化成 String
    gen.writeString(Long.toUnsignedString(value.getId()));
    // gen.writeStartObject();
    // gen.writeStringField("entityType", value.getEntityType().name());
    // gen.writeStringField("id", Long.toUnsignedString(value.getId()));
    // gen.writeEndObject();
  }
}

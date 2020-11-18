package wx.common.data.shared.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wx.common.data.shared.EntityType;

class EntityIdTest {

  @Test
  @Disabled("EntityId 现在被序列化成 id 字符串，丢失了类型信息")
  void testSerDeser() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String ser = objectMapper.writeValueAsString(new ApplicationId(10L));
    EntityId entityId = objectMapper.readValue(ser, EntityId.class);
    assertTrue(entityId instanceof ApplicationId);
    assertEquals(EntityType.APPLICATION, entityId.getEntityType());
    assertEquals(10L, entityId.getId());
  }

  @Test
  @Disabled("EntityId 现在被序列化成 id 字符串，丢失了类型信息")
  void testSerDeserMaxId() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String ser = objectMapper.writeValueAsString(new ApplicationId(Long.MAX_VALUE));
    EntityId entityId = objectMapper.readValue(ser, EntityId.class);
    assertTrue(entityId instanceof ApplicationId);
    assertEquals(EntityType.APPLICATION, entityId.getEntityType());
    assertEquals(Long.MAX_VALUE, entityId.getId());
  }
}

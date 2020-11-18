package wx.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ObjectStoreTest {
  @Test
  void testFileObjectIO(@TempDir File root) throws IOException {
    final ObjectStore objectStore = new ObjectStore(root);
    assertEquals(0, objectStore.getIds().size());
    final int total = 100;
    int cursize = 0;

    // add some
    for (int i = 0; i < total; i++) {
      final String objId = UUID.randomUUID().toString();
      objectStore.saveJSONObject(objId, objId);
      cursize = i + 1;
      assertEquals(cursize, objectStore.getIds().size());
    }

    // reload dir
    final ObjectStore objectStore1 = new ObjectStore(root);
    assertEquals(cursize, objectStore1.getIds().size());

    // remove some
    final ArrayList<String> toRemove = new ArrayList<>();
    objectStore1.getIds().stream().limit(20).forEach(toRemove::add);
    for (String id : toRemove) {
      objectStore1.remove(id);
      cursize--;
      assertEquals(cursize, objectStore1.getIds().size());
    }

    // reload dir
    final ObjectStore objectStore2 = new ObjectStore(root);
    assertEquals(cursize, objectStore2.getIds().size());

    // destroy triggers flush
    objectStore2.destroy();
    final ObjectStore objectStore3 = new ObjectStore(root);
    assertEquals(cursize, objectStore3.getIds().size());

    // read content
    for (String id : objectStore3.getIds()) {
      assertEquals(id, objectStore3.readJSONObject(id, String.class));
    }
  }
}

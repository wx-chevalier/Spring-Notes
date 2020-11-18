package wx.common.data.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyValue<K, V> {

  private K key;

  private V value;
}

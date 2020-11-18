package wx.common.data.infra.kv;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = {"key", "value"})
@ToString(of = {"key", "value"})
public abstract class BaseKvEntry<T> implements KvEntry<T> {
  private String key;
  private T value;

  BaseKvEntry(String key, T value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public T getValue() {
    return value;
  }
}

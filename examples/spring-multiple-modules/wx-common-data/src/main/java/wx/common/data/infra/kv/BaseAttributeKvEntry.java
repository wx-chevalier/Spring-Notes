package wx.common.data.infra.kv;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = {"kv", "lastUpdateTs"})
@ToString(of = {"kv", "lastUpdateTs"})
public class BaseAttributeKvEntry<T> implements AttributeKvEntry<T> {
  private final long lastUpdateTs;
  private final KvEntry<T> kv;

  public BaseAttributeKvEntry(long lastUpdateTs, KvEntry<T> kv) {
    this.lastUpdateTs = lastUpdateTs;
    this.kv = kv;
  }

  @Override
  public Long getLastUpdateTs() {
    return lastUpdateTs;
  }

  @Override
  public String getKey() {
    return kv.getKey();
  }

  @Override
  public DataType getType() {
    return kv.getType();
  }

  @Override
  public T getValue() {
    return kv.getValue();
  }
}

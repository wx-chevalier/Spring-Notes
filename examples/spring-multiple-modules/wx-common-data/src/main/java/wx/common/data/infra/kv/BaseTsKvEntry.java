package wx.common.data.infra.kv;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = {"ts", "kv"})
@ToString(of = {"ts", "kv"})
public class BaseTsKvEntry<T> implements TsKvEntry<T> {
  private final long ts;
  private final KvEntry<T> kv;

  public BaseTsKvEntry(long ts, KvEntry<T> kv) {
    this.ts = ts;
    this.kv = kv;
  }

  @Override
  public long getTs() {
    return ts;
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

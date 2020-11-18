package wx.common.data.infra.kv;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class LongEntry extends BaseKvEntry<Long> {

  public LongEntry(String key, Long value) {
    super(key, value);
  }

  @Override
  public DataType getType() {
    return DataType.LONG;
  }
}

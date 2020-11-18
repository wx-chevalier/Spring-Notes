package wx.common.data.infra.kv;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BooleanEntry extends BaseKvEntry<Boolean> {

  public BooleanEntry(String key, boolean value) {
    super(key, value);
  }

  @Override
  public DataType getType() {
    return DataType.BOOLEAN;
  }
}

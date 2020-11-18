package wx.common.data.infra.kv;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class DoubleEntry extends BaseKvEntry<Double> {

  public DoubleEntry(String key, Double value) {
    super(key, value);
  }

  @Override
  public DataType getType() {
    return DataType.DOUBLE;
  }
}

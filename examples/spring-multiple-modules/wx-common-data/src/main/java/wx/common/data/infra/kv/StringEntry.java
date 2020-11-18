package wx.common.data.infra.kv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class StringEntry extends BaseKvEntry<String> {

  @JsonCreator
  public StringEntry(@JsonProperty("key") String key, @JsonProperty("value") String value) {
    super(key, value);
  }

  @Override
  public DataType getType() {
    return DataType.STRING;
  }
}

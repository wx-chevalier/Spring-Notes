package wx.common.data.infra.kv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = BooleanEntry.class, name = "BOOLEAN"),
  @JsonSubTypes.Type(value = DoubleEntry.class, name = "DOUBLE"),
  @JsonSubTypes.Type(value = LongEntry.class, name = "LONG"),
  @JsonSubTypes.Type(value = StringEntry.class, name = "STRING")
})
public interface KvEntry<T> {
  static Optional<Long> ofLong(KvEntry kv) {
    if (kv.getType() == DataType.LONG) {
      return Optional.ofNullable((Long) kv.getValue());
    } else {
      return Optional.empty();
    }
  }

  static Optional<Boolean> ofBoolean(KvEntry kv) {
    if (kv.getType() == DataType.BOOLEAN) {
      return Optional.ofNullable((Boolean) kv.getValue());
    } else {
      return Optional.empty();
    }
  }

  static Optional<Double> ofDouble(KvEntry kv) {
    if (kv.getType() == DataType.DOUBLE) {
      return Optional.ofNullable((Double) kv.getValue());
    } else {
      return Optional.empty();
    }
  }

  static Optional<String> ofString(KvEntry kv) {
    if (kv.getType() == DataType.STRING) {
      return Optional.ofNullable((String) kv.getValue());
    } else {
      return Optional.empty();
    }
  }

  String getKey();

  DataType getType();

  T getValue();
}

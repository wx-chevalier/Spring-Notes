package wx.common.data.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.base.Charsets;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class AbstractJsonSerializer<T> {
  protected static final ObjectMapper objectMapper =
      new ObjectMapper()
          .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.DEFAULT)
          .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
          .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
          .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
          .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
          .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule());

  private final Class<T> type;

  protected AbstractJsonSerializer(Class<T> type) {
    this.type = type;
  }

  public T fromJson(String jsonString) {
    try {
      return objectMapper.readValue(jsonString.getBytes(Charsets.UTF_8), type);
    } catch (IOException e) {
      throw new UncheckedIOException(e.getMessage(), e);
    }
  }

  public String toJson(T t) {
    try {
      return objectMapper.writeValueAsString(t);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e.getMessage(), e);
    }
  }

  public @Nullable T fromJsonIgnoreError(String jsonString) {
    try {
      return objectMapper.readValue(jsonString.getBytes(Charsets.UTF_8), type);
    } catch (IOException e) {
      log.warn("{} Error deserializing from json: {}", getClass().getSimpleName(), jsonString);
      return null;
    }
  }

  public @Nullable String toJsonIgnoreError(T t) {
    try {
      return objectMapper.writeValueAsString(t);
    } catch (JsonProcessingException e) {
      log.warn("{} Error serializing to json: {}", getClass().getSimpleName(), t);
      return null;
    }
  }
}

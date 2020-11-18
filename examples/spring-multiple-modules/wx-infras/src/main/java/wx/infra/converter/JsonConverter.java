package wx.infra.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class JsonConverter {
  public static final ObjectMapper jsonObjectMapper = new ObjectMapper();

  static {
    jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static <T> T readJSON(File file, Class<T> clazz) {
    try {
      return jsonObjectMapper.readValue(file, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readJSON(File file, TypeReference<T> typeReference) {
    try {
      return jsonObjectMapper.readValue(file, typeReference);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> void writeJSONToFile(T val, File file) {
    try {
      jsonObjectMapper.writeValue(file, val);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readJSON(String json, Class<T> clazz) {
    if (!StringUtils.hasText(json)) {
      return null;
    }
    try {
      return jsonObjectMapper.readValue(json, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readJSON(String json, TypeReference<T> typeReference) throws IOException {
    if (json == null) {
      return null;
    }
    return jsonObjectMapper.readValue(json, typeReference);
  }

  /** 将序列化错误封装成运行时错误，期望调用者传入可序列化的数据 */
  public static <T> String toJSONString(T object) {
    if (Objects.isNull(object)) {
      return null;
    }
    try {
      return jsonObjectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readJSON(String json, Class<T> clazz, T defaultValue) {
    if (json == null) {
      return defaultValue;
    }
    try {
      return readJSON(json, clazz);
    } catch (Throwable e) {
      log.warn("JSON 读取错误: {}\n{}\n默认值：{}", clazz, json, defaultValue);
      log.warn("JSON 读取错误 Stacktrace", e);
      return defaultValue;
    }
  }

  public static <T> T readJSON(File file, Class<T> clazz, T defaultValue) {
    if (!file.exists()) {
      return defaultValue;
    }
    try {
      return jsonObjectMapper.readValue(file, clazz);
    } catch (IOException e) {
      log.warn("JSON 读取错误: {}\n{}\n默认值：{}", clazz, file, defaultValue);
      log.warn("JSON 读取错误 Stacktrace", e);
      return defaultValue;
    }
  }

  public static <T> T readJSON(String json, TypeReference<T> typeReference, T defaultValue) {
    if (json == null) {
      return defaultValue;
    }
    try {
      return readJSON(json, typeReference);
    } catch (IOException e) {
      log.warn("JSON 读取错误: {}\n{}\n默认值：{}", typeReference, json, defaultValue);
      log.warn("JSON 读取错误 Stacktrace", e);
      return defaultValue;
    }
  }

  /** 反序列化List<T> */
  public static <T> List<T> readListValue(String jsonStr, Class<T> valueType) {
    try {
      return jsonObjectMapper.readValue(jsonStr, getCollectionType(List.class, valueType));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** 获取泛型的集合类型 */
  public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
    return jsonObjectMapper
        .getTypeFactory()
        .constructParametricType(collectionClass, elementClasses);
  }
}

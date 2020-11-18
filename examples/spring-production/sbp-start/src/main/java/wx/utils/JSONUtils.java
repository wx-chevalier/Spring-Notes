package wx.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONUtils {
  private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

  public static <T> T readJSON(File file, Class<T> clazz, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(file, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readJSON(File file, Class<T> clazz) {
    return readJSON(file, clazz, jsonObjectMapper);
  }

  public static <T> T readJSON(Reader reader, Class<T> clazz, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(reader, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readJSON(Reader reader, Class<T> clazz) {
    return readJSON(reader, clazz, jsonObjectMapper);
  }

  public static void saveJSON(Object obj, File dstFile, ObjectMapper objectMapper) {
    try {
      objectMapper.writeValue(dstFile, obj);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void saveJSON(Object obj, File dstFile) {
    saveJSON(obj, dstFile, jsonObjectMapper);
  }

  public static String toJSON(Object obj, ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String toJSON(Object obj) {
    return toJSON(obj, jsonObjectMapper);
  }
}

package wx.domain.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {

  /**
   * @param dateTime LocalDateTime
   * @return ms
   */
  public static long toTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  /**
   * @param timestamp ms
   * @return LocalDateTime
   */
  public static LocalDateTime fromTimestamp(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
  }
}

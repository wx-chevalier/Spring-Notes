package wx.infra.db;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

public class DateTimeUtils {
  public static LocalDateTime fromTimestamp(long timestamp) {
    Instant instant = Instant.ofEpochMilli(timestamp);
    return LocalDateTime.ofInstant(
        instant, Clock.systemDefaultZone().getZone().getRules().getOffset(instant));
  }
}

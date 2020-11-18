package wx.domain.speed;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SpeedMeasurement {
  private final long read;
  private final long write;
  private final LocalDateTime sampledAt;

  public SpeedMeasurement(long read, long write, LocalDateTime sampledAt) {
    this.read = read;
    this.write = write;
    this.sampledAt = sampledAt;
  }
}

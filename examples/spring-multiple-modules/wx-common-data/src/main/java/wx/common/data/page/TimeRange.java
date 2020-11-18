package wx.common.data.page;

import java.time.LocalDateTime;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeRange {

  @Nullable private LocalDateTime startTime;

  @Nullable private LocalDateTime endTime;
}

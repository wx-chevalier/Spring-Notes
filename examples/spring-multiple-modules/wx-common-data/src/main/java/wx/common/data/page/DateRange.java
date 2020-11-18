package wx.common.data.page;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateRange {

  @Nullable private LocalDate startDate;

  @Nullable private LocalDate endDate;
}

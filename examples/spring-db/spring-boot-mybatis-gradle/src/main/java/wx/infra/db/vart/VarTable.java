package wx.infra.db.vart;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Data;
import wx.infra.db.DateTimeUtils;

@Data
@TableName("var_table")
public class VarTable {

  private String name;

  private long ts;

  private Integer integerVal;

  private Long longVal;

  private Double doubleVal;

  private Boolean booleanVal;

  private LocalDateTime datetimeVal;

  private LocalDate dateVal;

  private LocalDateTime timestampVal;

  @Override
  public String toString() {
    String res = "name: " + name + "\n";
    res += "  ts: " + ts + "\n";
    res += " *ts: " + DateTimeUtils.fromTimestamp(ts) + "\n";

    res += "  date time: " + datetimeVal + "\n";
    res += "  date time: " + datetimeVal.toInstant(ZoneOffset.UTC).toEpochMilli() + "\n";
    res +=
        " *date time: "
            + DateTimeUtils.fromTimestamp(datetimeVal.toInstant(ZoneOffset.UTC).toEpochMilli())
            + "\n";

    res += "  timestamp: " + timestampVal + "\n";
    res += "  timestamp: " + timestampVal.toInstant(ZoneOffset.UTC).toEpochMilli() + "\n";
    res +=
        " *timestamp: "
            + DateTimeUtils.fromTimestamp(timestampVal.toInstant(ZoneOffset.UTC).toEpochMilli())
            + "\n";

    return res;
  }
}

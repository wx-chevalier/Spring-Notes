package wx.api.config.properties;

import lombok.Data;
import wx.api.config.ApplicationDefaults;

@Data
public class TimeSeriesProperties {
  private long maxTsIntervals = ApplicationDefaults.maxTsIntervals;
}

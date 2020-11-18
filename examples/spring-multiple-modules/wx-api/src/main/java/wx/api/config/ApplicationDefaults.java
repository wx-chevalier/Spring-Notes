package wx.api.config;

public interface ApplicationDefaults {
  Integer jwtExpirationSec = 36000;

  // TimeSeries related configuration
  long maxTsIntervals = 700;
}

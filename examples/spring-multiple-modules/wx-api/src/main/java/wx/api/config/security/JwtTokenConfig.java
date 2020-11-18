package wx.api.config.security;

import lombok.Data;
import wx.api.config.ApplicationDefaults;

@Data
public class JwtTokenConfig {
  private String secret;

  private String issuer = "db";

  private Integer expirationSec = ApplicationDefaults.jwtExpirationSec;
}

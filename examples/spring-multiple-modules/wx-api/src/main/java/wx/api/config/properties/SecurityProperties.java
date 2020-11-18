package wx.api.config.properties;

import lombok.Data;
import wx.api.config.security.JwtTokenConfig;

/**
 * SecurityProperties.
 *
 * @author lotuc
 */
@Data
public class SecurityProperties {
  JwtTokenConfig jwt;
}

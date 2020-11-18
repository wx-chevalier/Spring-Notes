package wx.api.config.security.model;

import lombok.Getter;

public class AccessJwtToken implements JwtToken {
  @Getter private final String token;

  public AccessJwtToken(String token) {
    this.token = token;
  }
}

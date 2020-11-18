package wx.api.config.security.model;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import wx.infra.common.exception.UnAuthorizedException;

@Slf4j
public class RawAccessJwtToken implements JwtToken {
  @Getter private String token;

  public RawAccessJwtToken(String token) {
    this.token = token;
  }

  public Map<String, Claim> parseClaims(String signingKey) {
    try {
      DecodedJWT jwt =
          JWT.require(HMAC512(signingKey.getBytes(StandardCharsets.UTF_8))).build().verify(token);
      return jwt.getClaims();
    } catch (TokenExpiredException e) {
      log.info("JWT Token is expired", e);
      throw new UnAuthorizedException("Expired jwt token", e);
    } catch (JWTVerificationException e) {
      log.error("Invalid JWT token", e);
      throw new BadCredentialsException("Invalid jwt token", e);
    }
  }
}

package wx.api.config.security.model;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.interfaces.Claim;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import wx.api.config.security.JwtTokenConfig;
import wx.common.data.account.Authority;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.EntityIdFactory;
import wx.common.data.shared.id.UserId;

@Component
public class JwtTokenFactory {
  private static final String USER_ID = "userId";
  private static final String USERNAME = "username";
  private static final String TENANT_ID = "tenantId";
  private static final String SCOPES = "scopes";

  private static final String ENTITY_ID = "entityId";
  private static final String ENTITY_TYPE = "entityType";

  private JwtTokenConfig jwtTokenConfig;

  public JwtTokenFactory(JwtTokenConfig jwtTokenConfig) {
    this.jwtTokenConfig = jwtTokenConfig;
  }

  public AccessJwtToken createAccessJwtToken(SecurityUser user) {
    ZonedDateTime now = ZonedDateTime.now();
    Builder builder = JWT.create();
    builder
        .withIssuer(jwtTokenConfig.getIssuer())
        .withIssuedAt(Date.from(now.toInstant()))
        .withExpiresAt(
            Date.from(now.plusSeconds(jwtTokenConfig.getExpirationSec() * 1000).toInstant()))
        .withClaim(USER_ID, EntityId.getIdString(user.getId()))
        .withClaim(USERNAME, user.getUsername());
    if (user.getTenantId() != null) {
      builder.withClaim(TENANT_ID, EntityId.getIdString(user.getTenantId()));
    }
    builder.withArrayClaim(
        SCOPES, Stream.of(user.getAuthority()).map(Enum::name).toArray(String[]::new));
    String token = builder.sign(HMAC512(jwtTokenConfig.getSecret()));
    return new AccessJwtToken(token);
  }

  public SecurityUser parseAccessJwtToken(RawAccessJwtToken rawAccessJwtToken) {
    Map<String, Claim> claims = rawAccessJwtToken.parseClaims(jwtTokenConfig.getSecret());
    List<String> scopes = claims.get(SCOPES).asList(String.class);
    if (scopes == null || scopes.isEmpty()) {
      throw new IllegalArgumentException("JWT Token doesn't have any scopes");
    }
    Optional<Authority> authority = Authority.parse(scopes.get(0));
    if (!authority.isPresent()) {
      throw new IllegalArgumentException("Unrecognized JWT Token scopes: " + scopes);
    }
    UserId userId = new UserId(Long.parseUnsignedLong(claims.get(USER_ID).asString()));
    String username = claims.get(USERNAME).asString();
    TenantId tenantId = new TenantId(Long.parseUnsignedLong(claims.get(TENANT_ID).asString()));
    return new SecurityUser()
        .setId(userId)
        .setAuthority(authority.orElse(null))
        .setTenantId(tenantId)
        .setUsername(username);
  }

  public SecurityUser parseAccessKeyJwtToken(
      RawAccessJwtToken rawAccessJwtToken, String accessKey, String accessSecret) {
    Map<String, Claim> claims = rawAccessJwtToken.parseClaims(accessSecret);

    String entityId = claims.get(ENTITY_ID).asString();
    String entityType = claims.get(ENTITY_TYPE).asString();
    TenantId tenantId = new TenantId(Long.parseUnsignedLong(claims.get(TENANT_ID).asString()));

    return new SecurityUser()
        .setTenantId(tenantId)
        .setAccessKey(accessKey)
        .setEntityId(EntityIdFactory.getByTypeAndId(entityType, entityId));
  }
}

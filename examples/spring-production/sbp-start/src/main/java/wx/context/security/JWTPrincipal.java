package wx.context.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Sets;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import javax.ws.rs.ForbiddenException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import wx.constants.UserRole;

@Getter
@Slf4j
public class JWTPrincipal {
  private String userId;
  private String userName;
  private Set<UserRole> roles;
  private Set<String> authorities;

  public JWTPrincipal(
      String userId, String userName, Set<UserRole> roles, Set<String> authorities) {
    this.userId = userId;
    this.userName = userName;
    this.roles = roles;
    this.authorities = authorities;
  }

  public static JWTPrincipal verityJWTAndRetrievePrincipal(String jwtString, String secret) {
    DecodedJWT jwt;
    try {
      jwt =
          JWT.require(HMAC512(secret.getBytes(Charset.forName("UTF-8")))).build().verify(jwtString);
    } catch (Exception e) {
      log.warn("Invalid access token[{}]", jwtString);
      throw new AuthorizationServiceException("Invalid access token", e);
    }
    final String userId = jwt.getSubject();
    final String userName = jwt.getClaim("name").asString();
    final Set<UserRole> roles = new HashSet<>();
    final Set<String> authorities = new HashSet<>();
    for (String s : jwt.getClaim("authorities").asList(String.class)) {
      final Optional<UserRole> userRole = UserRole.fromRoleString(s);
      if (userRole.isPresent()) {
        roles.add(userRole.get());
      } else {
        authorities.add(s);
      }
    }
    return new JWTPrincipal(userId, userName, roles, authorities);
  }

  public static Optional<JWTPrincipal> getFromSecurityContext() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    final Object principal = authentication.getPrincipal();
    return Optional.ofNullable(principal instanceof JWTPrincipal ? (JWTPrincipal) principal : null);
  }

  public static JWTPrincipal getOrThrow() {
    return getFromSecurityContext().orElseThrow(ForbiddenException::new);
  }

  public List<SimpleGrantedAuthority> getGrantedAuthorities() {
    return Sets.union(
            roles.stream().map(UserRole::toRoleString).collect(Collectors.toSet()), authorities)
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  public String toJWTString(String secret, long expireMilliSeconds) {
    final GregorianCalendar expireAt = new GregorianCalendar();
    while (expireMilliSeconds > Integer.MAX_VALUE) {
      expireMilliSeconds -= Integer.MAX_VALUE;
      expireAt.add(Calendar.MILLISECOND, Integer.MAX_VALUE);
    }
    expireAt.add(GregorianCalendar.MILLISECOND, (int) expireMilliSeconds);

    final List<String> authorityClaims = new ArrayList<>();
    roles.forEach(role -> authorityClaims.add(role.toRoleString()));
    authorityClaims.addAll(authorities);

    return JWT.create()
        .withSubject(userId)
        .withClaim("name", userName)
        .withExpiresAt(expireAt.getTime())
        .withArrayClaim("authorities", authorityClaims.toArray(new String[] {}))
        .sign(HMAC512(secret.getBytes(Charset.forName("UTF-8"))));
  }
}

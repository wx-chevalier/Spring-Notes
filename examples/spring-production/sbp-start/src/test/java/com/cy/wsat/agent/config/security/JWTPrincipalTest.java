package wx.context.security;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import wx.constants.UserRole;

class JWTPrincipalTest {
  @Test
  void testJWTPrincipal() {
    final String secret = "secret";
    final JWTPrincipal principal =
        new JWTPrincipal(
            "user-id",
            "user-name",
            Sets.newHashSet(UserRole.ADMIN),
            Sets.newHashSet("hello", "world"));
    final String jwtString = principal.toJWTString(secret, 1000);
    final JWTPrincipal principalAfter =
        JWTPrincipal.verityJWTAndRetrievePrincipal(jwtString, secret);

    assertEquals(principal.getUserId(), principalAfter.getUserId());
    assertEquals(principal.getUserName(), principalAfter.getUserName());
    assertEquals(principal.getRoles(), principalAfter.getRoles());
    assertEquals(principal.getAuthorities(), principalAfter.getAuthorities());
  }
}

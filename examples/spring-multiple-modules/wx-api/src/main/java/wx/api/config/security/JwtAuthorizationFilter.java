package wx.api.config.security;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import wx.api.config.log.MdcKeys;
import wx.api.config.security.model.JwtTokenFactory;
import wx.api.config.security.model.RawAccessJwtToken;
import wx.api.config.security.model.SecurityUser;
import wx.common.data.shared.id.EntityId;
import wx.domain.auth.AccessKey;
import wx.domain.auth.AccessKeyRepository;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private JwtTokenFactory jwtTokenFactory;

  private AccessKeyRepository accessKeyRepository;

  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager,
      JwtTokenFactory jwtTokenFactory,
      AccessKeyRepository accessKeyRepository) {
    super(authenticationManager);
    this.jwtTokenFactory = jwtTokenFactory;
    this.accessKeyRepository = accessKeyRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String authorization = request.getHeader(SecurityConstants.AUTH_HEADER);

    if (authorization != null) {
      if (authorization.startsWith(SecurityConstants.BEARER)) {
        verifyBearerToken(authorization.substring(SecurityConstants.BEARER.length()));
      } else if (authorization.startsWith(SecurityConstants.UFC_ACCESS_KEY)) {
        verifyUfcAccessKey(authorization.substring(SecurityConstants.UFC_ACCESS_KEY.length()));
      }
    }

    chain.doFilter(request, response);
  }

  private void verifyUfcAccessKey(String ufcKeyToken) {
    String[] split = ufcKeyToken.split(":", 2);
    if (split.length == 2) {
      String key = split[0];
      String token = split[1];
      Optional<AccessKey> accessKey = accessKeyRepository.findByKey(key);
      if (accessKey.isPresent()) {
        SecurityUser securityUser =
            jwtTokenFactory.parseAccessKeyJwtToken(
                new RawAccessJwtToken(token), key, accessKey.get().getSecret());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                securityUser,
                null,
                Stream.of(securityUser.getAuthority())
                    .map(authority -> new SimpleGrantedAuthority(authority.name()))
                    .collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
  }

  private void verifyBearerToken(String token) {
    try {
      SecurityUser securityUser = jwtTokenFactory.parseAccessJwtToken(new RawAccessJwtToken(token));

      MDC.put(MdcKeys.USER_SUMMARY_ID, getSecurityUserMdcSummaryValue(securityUser));
      MDC.put(MdcKeys.USER_DETAIL_ID, getSecurityUserMdcDetailValue(securityUser));

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              securityUser,
              null,
              Stream.of(securityUser.getAuthority())
                  .map(authority -> new SimpleGrantedAuthority(authority.name()))
                  .collect(Collectors.toList()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception e) {
      log.debug("Invalid token", e);
    }
  }

  private String getSecurityUserMdcSummaryValue(SecurityUser securityUser) {
    return "["
        + EntityId.getIdString(securityUser.getTenantId())
        + ","
        + EntityId.getIdString(securityUser.getId())
        + "]";
  }

  private String getSecurityUserMdcDetailValue(SecurityUser securityUser) {
    return "[Tenant="
        + EntityId.getIdString(securityUser.getTenantId())
        + ",User="
        + EntityId.getIdString(securityUser.getId())
        + ":"
        + securityUser.getUsername()
        + "]";
  }
}

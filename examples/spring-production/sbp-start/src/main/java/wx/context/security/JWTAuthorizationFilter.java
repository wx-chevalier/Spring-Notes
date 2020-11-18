package wx.context.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import wx.context.properties.SecurityProperty;
import wx.utils.JSONUtils;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
  private SecurityProperty.JwtConfig jwt;
  private ObjectMapper objectMapper;

  JWTAuthorizationFilter(
      AuthenticationManager authenticationManager,
      SecurityProperty.JwtConfig jwt,
      ObjectMapper objectMapper) {
    super(authenticationManager);
    this.jwt = jwt;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String jwtHeader = request.getHeader(this.jwt.getTokenHeader());
    String jwtToken = null;
    if (jwtHeader != null) {
      log.debug("JWT Authorization Header is {}", jwtHeader);
      if (jwtHeader.startsWith(this.jwt.getTokenPrefix())) {
        jwtToken = jwtHeader.substring(this.jwt.getTokenPrefix().length());
      }
    }

    if (jwtToken != null) {
      try {
        final JWTPrincipal principal =
            JWTPrincipal.verityJWTAndRetrievePrincipal(jwtToken, this.jwt.getSecret());

        // 将获取到的用户权限写入到 SecurityContext 中
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getGrantedAuthorities()));
      } catch (AuthorizationServiceException e) {
        log.warn("JWT token authorization failed", e);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        final PrintWriter writer = response.getWriter();
        writer.write(
            JSONUtils.toJSON(
                new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage()), objectMapper));
        writer.close();
        return;
      }
    }

    chain.doFilter(request, response);
  }

  @Getter
  static class ErrorResponse {
    Integer status;
    String error;
    String message;

    ErrorResponse(HttpStatus status, String message) {
      this.status = status.value();
      this.error = status.getReasonPhrase();
      this.message = message;
    }
  }
}

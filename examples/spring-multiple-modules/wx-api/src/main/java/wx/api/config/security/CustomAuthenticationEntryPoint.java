package wx.api.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import wx.api.rest.shared.dto.envelope.ErrorMessage;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private ObjectMapper objectMapper;

  public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response
        .getWriter()
        .write(
            objectMapper.writeValueAsString(
                ResponseEnvelope.createErr(
                    new ErrorMessage(HttpStatus.UNAUTHORIZED.name(), "unauthorized"))));
  }
}

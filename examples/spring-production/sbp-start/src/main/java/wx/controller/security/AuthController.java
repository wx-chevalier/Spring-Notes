package wx.controller.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wx.context.properties.ApplicationProperty;
import wx.context.properties.SecurityProperty;
import wx.context.security.JWTPrincipal;
import wx.service.security.UserService;

@RestController
public class AuthController {
  private SecurityProperty.JwtConfig jwtConfig;
  private UserService userService;

  public AuthController(UserService userService, ApplicationProperty applicationProperty) {
    this.userService = userService;
    this.jwtConfig = applicationProperty.getSecurity().getJwt();
  }

  @PostMapping("/login")
  public Token login(@RequestBody LoginCredential credential) {
    final JWTPrincipal principal = userService.login(credential.username, credential.password);
    return new Token(principal.toJWTString(jwtConfig.getSecret(), jwtConfig.getExpirationMs()));
  }

  @ApiModel(description = "登陆验证信息")
  static class LoginCredential {
    @ApiModelProperty("用户名")
    @Getter
    String username;

    @ApiModelProperty("密码")
    String password;

    public LoginCredential(
        @JsonProperty("username") String username, @JsonProperty("password") String password) {
      this.username = username;
      this.password = password;
    }
  }

  @ApiModel(description = "登陆验证返回值")
  static class Token {
    @ApiModelProperty("JWT token")
    @Getter
    String token;

    public Token(String token) {
      this.token = token;
    }
  }
}

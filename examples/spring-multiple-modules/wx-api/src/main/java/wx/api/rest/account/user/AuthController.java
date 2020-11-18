package wx.api.rest.account.user;

import static wx.api.rest.shared.dto.envelope.ResponseEnvelope.createOk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import wx.api.config.security.model.AccessJwtToken;
import wx.api.config.security.model.JwtTokenFactory;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;
import wx.domain.account.UserRepository;
import wx.infra.common.exception.BadRequestException;
import wx.infra.service.aliyun.AliOssService;
import wx.infra.service.aliyun.model.AliOssAuth;

@RestController
@RequestMapping
@Api(tags = "🖥 身份验证")
public class AuthController extends BaseController {

  private UserRepository userRepository;
  private JwtTokenFactory jwtTokenFactory;
  private SecurityUserConverter securityUserConverter;
  private AliOssService aliOssService;

  public AuthController(
      UserRepository userRepository,
      JwtTokenFactory jwtTokenFactory,
      SecurityUserConverter securityUserConverter,
      AliOssService aliOssService) {
    this.userRepository = userRepository;
    this.jwtTokenFactory = jwtTokenFactory;
    this.securityUserConverter = securityUserConverter;
    this.aliOssService = aliOssService;
  }

  @GetMapping("/auth/user")
  @ApiOperation(value = "获取当前用户的个人信息")
  public ResponseEnvelope<User> getUserInfo() {
    return createOk(userQueryService.findUserDetail(getTenantId(), getCurrentUser().getId()));
  }

  @PatchMapping("/noauth/reset_password")
  @ApiOperation(value = "密码重置接口")
  public ResponseEnvelope<Void> reset(@RequestBody @Valid UserPasswordResetRequest request) {
    if (request.getUsername() == null && request.getPhoneNumber() == null) {
      throw new BadRequestException("username not set");
    }
    if (request.getVerificationCode() == null && request.getValidateCode() == null) {
      throw new BadRequestException("verification code not set");
    }
    String username =
        request.getUsername() != null ? request.getUsername() : request.getPhoneNumber();
    String verificationCode =
        request.getVerificationCode() != null
            ? request.getVerificationCode()
            : request.getValidateCode();
    userCommandService.resetPasswordViaVerificationCode(
        username, request.getPassword(), verificationCode);
    return createOk();
  }

  @PostMapping("/noauth/login")
  @ApiOperation(value = "用户登录")
  public ResponseEnvelope<AccessJwtToken> login(@Valid @RequestBody LoginRequest request) {
    UserId userId;
    switch (request.getCredentialType()) {
      case PASSWORD:
        userId =
            userCommandService.validateCredentials(request.getUsername(), request.getCredential());
        break;
      case VERIFICATION_CODE:
        userId =
            userCommandService.validateVerificationCodeCredentials(
                request.getUsername(), request.getCredential());
        break;
      default:
        throw new BadRequestException("credentialType");
    }
    return userRepository
        .findById(userId)
        .map(securityUserConverter::convertTo)
        .map(jwtTokenFactory::createAccessJwtToken)
        .map(ResponseEnvelope::createOk)
        .orElseThrow(() -> new BadRequestException("Invalid user or password"));
  }

  @GetMapping("/oss_auth")
  @ApiOperation(value = "获取阿里云OSS的临时授权")
  public ResponseEnvelope<AliOssAuth> aliYunOSSAuth() {
    return createOk(aliOssService.createAuth(EntityId.getIdString(getTenantId())));
  }
}

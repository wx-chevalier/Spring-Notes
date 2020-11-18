package wx.api.rest.account.user;

import static wx.api.rest.shared.dto.envelope.ResponseEnvelope.createOk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import wx.api.config.security.model.AccessJwtToken;
import wx.api.config.security.model.JwtTokenFactory;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.converter.PageConverter;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.application.account.user.UserDetail;
import wx.application.account.user.UserQuery;
import wx.common.data.account.Authority;
import wx.common.data.page.PageNumBasedPageLink;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;
import wx.domain.account.UserRepository;
import wx.domain.wechat.WechatQrCodeTicket;
import wx.domain.wechat.WechatQrCodeTicketRepository;
import wx.infra.common.exception.BadRequestException;
import wx.infra.common.exception.UnAuthorizedException;

@RestController
@RequestMapping("/user")
@Api(tags = "🖥 账户")
public class UserController extends BaseController {

  private WechatQrCodeTicketRepository wechatQrCodeTicketRepository;

  private UserRepository userRepository;

  private JwtTokenFactory jwtTokenFactory;

  public UserController(
      WechatQrCodeTicketRepository wechatQrCodeTicketRepository,
      UserRepository userRepository,
      JwtTokenFactory jwtTokenFactory) {
    this.wechatQrCodeTicketRepository = wechatQrCodeTicketRepository;
    this.userRepository = userRepository;
    this.jwtTokenFactory = jwtTokenFactory;
  }

  @PatchMapping("/login")
  @ApiOperation("更新当前用户的Token")
  public ResponseEnvelope<AccessJwtToken> refreshToken() {
    return tryGetCurrentUser()
        .map(jwtTokenFactory::createAccessJwtToken)
        .map(ResponseEnvelope::createOk)
        .orElseThrow(UnAuthorizedException::new);
  }

  @PostMapping
  @ApiOperation(value = "新增用户")
  public ResponseEnvelope<User> add(@Valid @RequestBody UserAddRequest userAddRequest) {

    Set<RoleId> roleIds =
        userAddRequest.getRoleIds().stream().map(RoleId::new).collect(Collectors.toSet());

    return createOk(
        userCommandService.addUser(
            getTenantId(),
            getCurrentUserId(),
            roleIds,
            new User(
                getTenantId(),
                userAddRequest.getUsername(),
                Authority.TENANT_USER,
                userAddRequest.getNickname(),
                userAddRequest.getPhoneNumber(),
                userAddRequest.getEmail(),
                userAddRequest.getRemark(),
                null,
                getCurrentUser().getId()),
            userAddRequest.getPassword()));
  }

  @PatchMapping("/{userId}")
  @ApiOperation("更新用户信息")
  public ResponseEnvelope<User> update(
      @PathVariable("userId") String userIdStr, @RequestBody UserUpdateRequest req) {

    UserId userId = UserId.create(userIdStr);
    UserId currentUserId = getCurrentUserId();
    TenantId tenantId = getTenantId();

    return userRepository
        .findById(tenantId, userId)
        .map(
            user -> {
              if (StringUtils.hasText(req.getUsername())) {
                user.setUsername(req.getUsername());
              }

              if (StringUtils.hasText(req.getEmail())) {
                user.setEmail(req.getEmail());
              }

              if (StringUtils.hasText(req.getPhoneNumber())) {
                user.setPhoneNumber(req.getPhoneNumber());
              }

              if (StringUtils.hasText(req.getRemark())) {
                user.setRemark(req.getRemark());
              }

              if (StringUtils.hasText(req.getNickname())) {
                user.setNickname(req.getNickname());
              }
              if (StringUtils.hasText(req.getAvatarFileId())) {
                user.setAvatarFileId(new FileId(req.getAvatarFileId()));
              }
              if (req.getAuthority() != null) {
                user.setAuthority(req.getAuthority());
              }
              user.setCreatorId(currentUserId);
              return user;
            })
        .map(
            user ->
                userCommandService.update(
                    user.getTenantId(), user, req.getPassword(), req.getRoleIds()))
        .map(ResponseEnvelope::createOk)
        .orElseThrow(() -> new BadRequestException("User not found: " + userId));
  }

  @GetMapping
  @ApiOperation(value = "获取当前用户的个人信息")
  public ResponseEnvelope<UserDetail> getUserInfo() {
    return createOk(userQueryService.findUserDetail(getTenantId(), getCurrentUser().getId()));
  }

  @PostMapping("/search")
  @ApiOperation(value = "用户列表")
  public ResponseEnvelope<List<UserDetail>> list(@RequestBody UserQueryRequest query) {

    String tenantId = query.getTenantId();
    String roleId = query.getRoleId();

    return PageConverter.toResponseEntity(
        userQueryService.findUserDetails(
            new UserQuery(
                StringUtils.hasText(tenantId) ? new TenantId(tenantId) : getTenantId(),
                query.getSearchText(),
                StringUtils.hasText(roleId) ? new RoleId(roleId) : null,
                new PageNumBasedPageLink(query.getPageNum(), query.getPageSize()))));
  }

  @DeleteMapping("/{userId}")
  @ApiOperation(value = "删除当前租户下指定用户")
  public ResponseEnvelope<Void> removeUser(@PathVariable("userId") String userId) {
    userCommandService.removeUser(getTenantId(), UserId.create(userId), getCurrentUserId());
    return createOk();
  }

  @PatchMapping("/phone_number")
  @ApiOperation(value = "更新绑定的手机号码")
  public ResponseEnvelope<Void> updatePhoneNumber(
      @RequestBody @Valid UserPhoneNumberUpdateRequest request) {
    TenantId tenantId = getTenantId();
    UserId userId = getCurrentUserId();
    String newPhoneNumber = request.getPhoneNumber();
    String code = request.getVerificationCode();
    String password = request.getPassword();

    userCommandService.resetPhoneNumber(tenantId, userId, newPhoneNumber, code, password);
    return createOk();
  }

  @PatchMapping("/email")
  @ApiOperation(value = "更新绑定的电子邮箱(发送绑定的邮件到目标邮箱)", notes = "本接口逻辑仅为发送绑定邮箱的邮件")
  public ResponseEnvelope<Void> updateEmail(@RequestBody @Valid UserEmailUpdateRequest request) {
    TenantId tenantId = getTenantId();
    UserId userId = getCurrentUserId();
    String newEmailAddress = request.getEmail();
    String password = request.getPassword();
    userCommandService.resetEmailAddress(tenantId, userId, newEmailAddress, password);
    return createOk();
  }

  @PatchMapping("/password")
  @ApiOperation("更新当前账户的密码")
  public ResponseEnvelope<Void> updatePassword(
      @RequestBody @Valid UserPasswordUpdateRequest request) {
    UserId userId = getCurrentUserId();
    TenantId tenantId = getTenantId();
    String currentPassword = request.getCurrentPassword();
    String newPassword = request.getNewPassword();

    userCommandService.resetPassword(tenantId, userId, currentPassword, newPassword);
    return createOk();
  }

  @GetMapping("/qr_code")
  @ApiOperation("获取绑定为二维码")
  public ResponseEnvelope<WechatQrCodeTicket> generatorCode() {
    WechatQrCodeTicket codeTicket =
        wechatQrCodeTicketRepository.getBoundQrCode(getTenantId(), getCurrentUserId());
    return ResponseEnvelope.createOk(codeTicket);
  }
}

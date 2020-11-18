package wx.application.account.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import wx.common.data.account.UserConfigKey;
import wx.common.data.account.UserConfigStatus;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.mq.MessageQueueName;
import wx.common.data.mq.notice.SendUserMessage;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.*;
import wx.domain.infra.verificationcode.VerificationCode;
import wx.domain.infra.verificationcode.VerificationCodeCommandService;
import wx.infra.common.exception.*;
import wx.infra.tunnel.db.account.*;

@Service
public class UserCommandServiceImpl implements UserCommandService {

  private static final UnAuthorizedException UN_AUTHORIZED_EXCEPTION =
      new UnAuthorizedException("Invalid user or password");
  private static final ForbiddenException FORBIDDEN_EXCEPTION =
      new ForbiddenException("Invalid credentials");

  private UserRepository userRepository;

  private AccessControlCommandService accessControlCommandService;

  private UserTunnel userTunnel;

  private UserCredentialsRepository userCredentialsRepository;

  private UserCredentialsTunnel userCredentialsTunnel;

  private UserConfigStatusTunnel configStatusTunnel;

  private AmqpTemplate amqpTemplate;

  private VerificationCodeCommandService verificationCodeCommandService;

  public UserCommandServiceImpl(
      UserRepository userRepository,
      AccessControlCommandService accessControlCommandService,
      UserTunnel userTunnel,
      UserCredentialsRepository userCredentialsRepository,
      UserCredentialsTunnel userCredentialsTunnel,
      UserConfigStatusTunnel configStatusTunnel,
      AmqpTemplate amqpTemplate,
      VerificationCodeCommandService verificationCodeCommandService) {
    this.userRepository = userRepository;
    this.accessControlCommandService = accessControlCommandService;
    this.userTunnel = userTunnel;
    this.userCredentialsRepository = userCredentialsRepository;
    this.userCredentialsTunnel = userCredentialsTunnel;
    this.configStatusTunnel = configStatusTunnel;
    this.amqpTemplate = amqpTemplate;
    this.verificationCodeCommandService = verificationCodeCommandService;
  }

  @Override
  public UserId validateCredentials(String username, String password) {
    return userRepository
        .findByUsername(username)
        .map(User::getId)
        .map(userId -> userCredentialsRepository.findByUserId(userId))
        .orElseThrow(() -> UN_AUTHORIZED_EXCEPTION)
        .map(userCredentials -> this.verifyUserCredentials(userCredentials, password))
        .orElseThrow(() -> UN_AUTHORIZED_EXCEPTION)
        .getUserId();
  }

  @Override
  public UserId validateVerificationCodeCredentials(String username, String code) {

    boolean verifyResult =
        verificationCodeCommandService.verifyCode(
            username, code, NoticeSendChannel.SMS, NoticeType.SMS_LOGIN);

    if (!verifyResult) {
      throw FORBIDDEN_EXCEPTION;
    }

    return userRepository
        .findByUsername(username)
        .map(User::getId)
        .orElseThrow(() -> UN_AUTHORIZED_EXCEPTION);
  }

  @Override
  public UserId validateCredentials(TenantId tenantId, String username, String password) {
    return userRepository
        .findByUsername(tenantId, username)
        .map(User::getId)
        .map(userId -> userCredentialsRepository.findByUserId(tenantId, userId))
        .orElseThrow(() -> UN_AUTHORIZED_EXCEPTION)
        .map(userCredentials -> this.verifyUserCredentials(userCredentials, password))
        .orElseThrow(() -> UN_AUTHORIZED_EXCEPTION)
        .getUserId();
  }

  @Override
  @Transactional
  public User update(
      TenantId tenantId, User user, @Nullable String password, @Nullable List<String> roleIds) {

    // 保存用户
    userRepository.save(tenantId, user);

    // 更新密码
    if (StringUtils.hasText(password)) {
      resetPassword(tenantId, user.getId(), password);
    }

    // 更新角色
    if (!CollectionUtils.isEmpty(roleIds)) {
      accessControlCommandService.addUserRole(
          tenantId,
          user.getId(),
          roleIds.stream().map(RoleId::new).collect(Collectors.toList()),
          user.getCreatorId());
    }
    return userRepository.assertExists(tenantId, user.getId());
  }

  @Override
  @Transactional
  public User addUser(
      TenantId tenantId, UserId creatorId, Set<RoleId> roleIds, User user, String password) {

    // 判断用户是否存在
    boolean exit = userRepository.findByUsername(user.getUsername()).isPresent();
    if (exit) {
      throw new NotAcceptException("新增失败，用户信息已存在");
    }

    // 通过手机号码判定用户是否存在
    if (StringUtils.hasText(user.getPhoneNumber())) {
      if (userRepository.findByUsername(tenantId, user.getPhoneNumber()).isPresent()) {
        throw new NotAcceptException("新增失败, 手机号码已被绑定");
      }
    }

    // 保存用户
    User savedUser = userRepository.save(user.getTenantId(), user);

    // 保存用户密码
    userCredentialsRepository.save(
        user.getTenantId(),
        new UserCredentials(
            savedUser.getTenantId(), savedUser.getId(), true, password, null, null));

    // 保存用户角色授权信息
    accessControlCommandService.addUserRole(tenantId, savedUser.getId(), roleIds, creatorId);
    return savedUser;
  }

  @Override
  @Transactional
  public void removeUser(TenantId tenantId, UserId userId, UserId currentId) {
    if (userId == currentId) {
      throw new NotAcceptException("操作失败, 不允许删除当前用户");
    }
    userRepository.removeById(tenantId, userId);
    userCredentialsRepository.removeByUserId(tenantId, userId);
  }

  @Override
  @Transactional
  public void resetPasswordViaVerificationCode(String username, String newPassword, String code) {

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundException("user not found: " + username));

    UserId userId = user.getId();

    if (verificationCodeCommandService.verifyCode(
        user.getPhoneNumber(), code, NoticeSendChannel.SMS, NoticeType.RESET)) {
      throw new BadRequestException("Invalid verification code");
    }
    UserCredentials userCredentials =
        userCredentialsRepository
            .findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    userCredentials.setPassword(newPassword);
    userCredentialsRepository.save(userCredentials.getTenantId(), userCredentials);
  }

  @Override
  @Transactional
  public void resetPassword(
      TenantId tenantId, UserId userId, String currentPassword, String newPassword) {
    UserCredentials userCredentials =
        userCredentialsRepository
            .findByUserId(tenantId, userId)
            .orElseThrow(NotFoundException::new);
    if (!Objects.equals(userCredentials.getPassword(), currentPassword)) {
      throw FORBIDDEN_EXCEPTION;
    }
    userCredentials.setPassword(newPassword);
    userCredentialsRepository.save(userCredentials.getTenantId(), userCredentials);
  }

  @Override
  @Transactional
  public void resetPhoneNumber(
      TenantId tenantId,
      UserId userId,
      String phoneNumber,
      String verificationCode,
      String password) {

    userRepository.assertExists(tenantId, userId);

    boolean verifyResult =
        verificationCodeCommandService.verifyCode(
            phoneNumber, verificationCode, NoticeSendChannel.SMS, NoticeType.BIND_PHONE);
    if (!verifyResult) {
      throw new NotFoundException("更新失败, 验证码不正确");
    }

    Boolean passwordRight = userCredentialsTunnel.checkPassword(userId, password);
    if (!passwordRight) {
      throw new NotFoundException("更新失败，密码不正确");
    }

    // 更新数据
    UserDO userDO = new UserDO();
    userDO.setId(userId.getId()).setPhoneNumber(phoneNumber);
    userTunnel.updateById(userDO);
  }

  @Override
  @Transactional
  public void resetEmailAddress(
      TenantId tenantId, UserId userId, String newEmailAddress, String password) {

    // 检查发送的邮件的个数
    int sendCount =
        verificationCodeCommandService.getSendCount(
            60, newEmailAddress, NoticeType.BIND_EMAIL, NoticeSendChannel.EMAIL);
    if (sendCount != 0) {
      throw new NotAcceptException("邮件发送频率过高，请稍后重试");
    }

    // 判定用户存在
    userRepository.assertExists(tenantId, userId);

    // 校验密码
    Boolean exist = userCredentialsTunnel.checkPassword(userId, password);

    if (!exist) {
      throw new NotFoundException("更新失败，密码不正确");
    }

    // 作废用户未使用的配置 & 持久化用户的配置
    configStatusTunnel.cleanUnused(tenantId, userId, UserConfigKey.EMAIL);
    configStatusTunnel.save(tenantId, userId, UserConfigKey.EMAIL, newEmailAddress);

    // TODO 构造参数模型 param
    // 发送绑定邮箱的验证码邮件
    SendUserMessage msg = new SendUserMessage();
    msg.setNoticeType(NoticeType.BIND_EMAIL)
        .setUserId(userId)
        .setChannel(NoticeSendChannel.EMAIL)
        .setParam(null);
    amqpTemplate.convertAndSend(MessageQueueName.SEND_MESSAGE, msg);
  }

  @Override
  @Transactional
  public void bindEmailAddress(String codeStr) {
    VerificationCode code =
        verificationCodeCommandService.getByCode(NoticeType.BIND_EMAIL, codeStr);

    if (code == null) {
      throw new NotFoundException("更新失败，验证码不存在");
    }

    // 更新用户的信息
    UserId userId =
        Optional.ofNullable(code.getUserId())
            .orElseThrow(() -> new NotFoundException("更新失败,用户ID获取失败"));

    // 获取用户的配置
    UserConfigStatusDO latestConfig = configStatusTunnel.getLatest(userId, UserConfigKey.EMAIL);

    // 更新用户的邮箱 & 更新配置为已使用
    UserDO entity = new UserDO().setId(userId.getId()).setEmail(latestConfig.getValue());
    userTunnel.updateById(entity);
    latestConfig.setStatus(UserConfigStatus.USED).setDeletedAt(LocalDateTime.now());
    configStatusTunnel.updateById(latestConfig);
  }

  private UserCredentials verifyUserCredentials(UserCredentials userCredentials, String password) {
    if (!Objects.equals(userCredentials.getPassword(), password)) {
      throw UN_AUTHORIZED_EXCEPTION;
    }
    return userCredentials;
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean checkPassword(UserId userId, String password) {
    return userCredentialsTunnel.checkPassword(userId, password);
  }

  private void resetPassword(TenantId tenantId, UserId userId, String newPassword) {
    UserCredentials userCredentials =
        userCredentialsRepository
            .findByUserId(tenantId, userId)
            .orElseThrow(NotFoundException::new);
    userCredentials.setPassword(newPassword);
    userCredentialsRepository.save(userCredentials.getTenantId(), userCredentials);
  }
}

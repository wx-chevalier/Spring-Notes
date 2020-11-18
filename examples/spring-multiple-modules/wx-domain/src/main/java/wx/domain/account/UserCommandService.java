package wx.domain.account;

import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;

public interface UserCommandService {

  /**
   * 用户通过用户名和密码登录
   *
   * <p>TODO: 多租户情况抛出异常
   */
  UserId validateCredentials(String username, String password);

  /** 用户通过租户，用户名以及密码登录 */
  UserId validateCredentials(TenantId tenantId, String username, String password);

  /**
   * 用户通过用户名以及验证码登录
   *
   * <p>此时的用户名实际为用户的手机号码</>
   *
   * <p>TODO: 多租户情况抛出异常</>
   */
  UserId validateVerificationCodeCredentials(String username, String code);

  User update(
      TenantId tenantId, User user, @Nullable String password, @Nullable List<String> roleIds);

  User addUser(
      TenantId tenantId, UserId creatorId, Set<RoleId> roleIds, User user, String password);

  void removeUser(TenantId tenantId, UserId userId, UserId currentId);

  void resetPasswordViaVerificationCode(String username, String newPassword, String code);

  void resetPassword(TenantId tenantId, UserId userId, String currentPassword, String newPassword);

  /** 重设手机号码 */
  void resetPhoneNumber(
      TenantId tenantId,
      UserId userId,
      String phoneNumber,
      String verificationCode,
      String password);

  /** 重置邮箱地址(发送验证邮件) */
  void resetEmailAddress(TenantId tenantId, UserId userId, String newEmail, String password);

  /** 绑定邮箱地址(校验绑定邮箱) */
  void bindEmailAddress(String linkCode);

  /** 校验用户的密码是否正确 */
  Boolean checkPassword(UserId userId, String password);
}

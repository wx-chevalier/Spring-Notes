package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.UserCredentialsMapper;

@Slf4j
@Component
public class UserCredentialsTunnel extends ServiceImpl<UserCredentialsMapper, UserCredentialsDO> {
  public List<UserCredentialsDO> listByUser(Long tenantId, Long userId) {
    return list(
        Helper.userCredentialsQ()
            .eq(UserCredentialsDO::getTenantId, tenantId)
            .eq(UserCredentialsDO::getUserId, userId)
            .eq(UserCredentialsDO::getIsEnabled, Boolean.TRUE)
            .isNull(UserCredentialsDO::getDeletedAt));
  }

  /** 禁用某用户的凭证 */
  public void disabledByUserId(Long userId) {
    UserCredentialsDO entity = new UserCredentialsDO();
    entity.setUserId(userId);
    entity.setIsEnabled(Boolean.FALSE);
    entity.setDeletedAt(LocalDateTime.now());

    Wrapper<UserCredentialsDO> wrapper =
        Helper.getQueryWrapper(UserCredentialsDO.class)
            .eq(UserCredentialsDO::getUserId, userId)
            .isNull(UserCredentialsDO::getDeletedAt);
    update(entity, wrapper);
  }

  // 检查账户信息是否正确
  public Boolean checkPassword(UserId userId, String password) {
    Wrapper<UserCredentialsDO> queryWrapper =
        Helper.getQueryWrapper(UserCredentialsDO.class)
            .eq(UserCredentialsDO::getUserId, userId.getId())
            .eq(UserCredentialsDO::getIsEnabled, Boolean.TRUE)
            .isNotNull(UserCredentialsDO::getPassword)
            .isNull(UserCredentialsDO::getDeletedAt);
    // TODO 周涛 使用加密后的密码校验密码是否匹配
    return list(queryWrapper).stream()
        .anyMatch(credentials -> Objects.equals(password, credentials.getPassword()));
  }

  public Optional<UserCredentialsDO> getByUserId(UserId userId) {
    return Optional.ofNullable(
        getOne(
            new LambdaQueryWrapper<UserCredentialsDO>()
                .isNull(UserCredentialsDO::getDeletedAt)
                .eq(UserCredentialsDO::getUserId, userId.getId())));
  }

  public Optional<UserCredentialsDO> getByUserId(TenantId tenantId, UserId userId) {
    return Optional.ofNullable(
        getOne(
            new LambdaQueryWrapper<UserCredentialsDO>()
                .isNull(UserCredentialsDO::getDeletedAt)
                .eq(UserCredentialsDO::getTenantId, tenantId.getId())
                .eq(UserCredentialsDO::getUserId, userId.getId())));
  }

  public Optional<UserCredentialsDO> getByActivateToken(TenantId tenantId, String activateToken) {
    return Optional.ofNullable(
        getOne(
            new LambdaQueryWrapper<UserCredentialsDO>()
                .isNull(UserCredentialsDO::getDeletedAt)
                .eq(UserCredentialsDO::getTenantId, tenantId.getId())
                .eq(UserCredentialsDO::getActivateToken, activateToken)));
  }

  public Optional<UserCredentialsDO> getByResetToken(TenantId tenantId, String resetToken) {
    return Optional.ofNullable(
        getOne(
            new LambdaQueryWrapper<UserCredentialsDO>()
                .isNull(UserCredentialsDO::getDeletedAt)
                .eq(UserCredentialsDO::getTenantId, tenantId.getId())
                .eq(UserCredentialsDO::getResetToken, resetToken)));
  }
}

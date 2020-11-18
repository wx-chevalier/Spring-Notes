package wx.application.account.accesscontrol;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserCredentialsId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.UserCredentials;
import wx.domain.account.UserCredentialsRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.account.UserCredentialsDO;
import wx.infra.tunnel.db.account.UserCredentialsTunnel;
import wx.infra.tunnel.db.mapper.account.UserCredentialsMapper;

@Repository
public class UserCredentialsRepositoryImpl
    extends MyBatisIdBasedEntityRepository<
        UserCredentialsTunnel,
        UserCredentialsMapper,
        UserCredentialsDO,
        UserCredentials,
        UserCredentialsId>
    implements UserCredentialsRepository {

  @Getter(AccessLevel.PROTECTED)
  private UserCredentialsConverter converter;

  public UserCredentialsRepositoryImpl(
      UserCredentialsTunnel userCredentialsTunnel,
      UserCredentialsMapper mapper,
      UserCredentialsConverter converter) {
    super(userCredentialsTunnel, mapper);
    this.converter = converter;
  }

  @Override
  public Optional<UserCredentials> findByUserId(UserId userId) {
    return getTunnel().getByUserId(userId).map(getConverter()::convertFrom);
  }

  @Override
  public Optional<UserCredentials> findByUserId(TenantId tenantId, UserId userId) {
    return getTunnel().getByUserId(tenantId, userId).map(getConverter()::convertFrom);
  }

  @Override
  public Optional<UserCredentials> findByActivateToken(TenantId tenantId, String activateToken) {
    return getTunnel().getByActivateToken(tenantId, activateToken).map(getConverter()::convertFrom);
  }

  @Override
  public Optional<UserCredentials> findByResetToken(TenantId tenantId, String resetToken) {
    return getTunnel().getByResetToken(tenantId, resetToken).map(getConverter()::convertFrom);
  }

  @Override
  public boolean removeByUserId(TenantId tenantId, UserId userId) {
    return getTunnel()
        .update(
            new LambdaUpdateWrapper<UserCredentialsDO>()
                .isNull(UserCredentialsDO::getDeletedAt)
                .eq(UserCredentialsDO::getUserId, userId.getId())
                .eq(UserCredentialsDO::getTenantId, tenantId.getId())
                .set(UserCredentialsDO::getDeletedAt, LocalDateTime.now()));
  }
}

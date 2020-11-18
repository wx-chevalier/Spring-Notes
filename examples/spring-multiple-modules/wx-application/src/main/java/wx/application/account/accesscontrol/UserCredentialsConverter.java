package wx.application.account.accesscontrol;

import java.util.Optional;
import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserCredentialsId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.UserCredentials;
import wx.infra.tunnel.db.account.UserCredentialsDO;

@Component
public class UserCredentialsConverter
    extends AbstractConverter<UserCredentials, UserCredentialsDO> {

  @Override
  public UserCredentialsDO convertTo(UserCredentials userCredentials) {
    return new UserCredentialsDO()
        .setId(
            Optional.ofNullable(userCredentials.getId()).map(UserCredentialsId::getId).orElse(null))
        .setTenantId(userCredentials.getTenantId().getId())
        .setUserId(userCredentials.getUserId().getId())
        .setIsEnabled(userCredentials.isEnabled())
        .setPassword(userCredentials.getPassword())
        .setActivateToken(userCredentials.getActivateToken())
        .setResetToken(userCredentials.getResetToken());
  }

  @Override
  public UserCredentials convertFrom(UserCredentialsDO userCredentialsDO) {
    return new UserCredentials(
        new UserCredentialsId(userCredentialsDO.getId()),
        new TenantId(userCredentialsDO.getTenantId()),
        userCredentialsDO.getCreatedAt(),
        userCredentialsDO.getUpdatedAt(),
        new UserId(userCredentialsDO.getUserId()),
        userCredentialsDO.getIsEnabled(),
        userCredentialsDO.getPassword(),
        userCredentialsDO.getActivateToken(),
        userCredentialsDO.getResetToken());
  }
}

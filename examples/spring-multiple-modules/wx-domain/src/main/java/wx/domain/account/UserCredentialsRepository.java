package wx.domain.account;

import java.util.Optional;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserCredentialsId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntityRepository;

public interface UserCredentialsRepository
    extends IdBasedEntityRepository<UserCredentialsId, UserCredentials> {

  Optional<UserCredentials> findByUserId(UserId userId);

  Optional<UserCredentials> findByUserId(TenantId tenantId, UserId userId);

  Optional<UserCredentials> findByActivateToken(TenantId tenantId, String activateToken);

  Optional<UserCredentials> findByResetToken(TenantId tenantId, String resetToken);

  boolean removeByUserId(TenantId tenantId, UserId userId);
}

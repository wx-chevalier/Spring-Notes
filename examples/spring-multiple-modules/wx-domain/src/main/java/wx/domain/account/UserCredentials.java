package wx.domain.account;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserCredentialsId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCredentials extends IdBasedEntity<UserCredentialsId, UserCredentials> {

  private UserId userId;

  private boolean enabled;

  private String password;

  private String activateToken;

  private String resetToken;

  public UserCredentials(
      UserCredentialsId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      UserId userId,
      boolean enabled,
      String password,
      String activateToken,
      String resetToken) {
    super(id, tenantId, createdAt, updatedAt);
    this.userId = userId;
    this.enabled = enabled;
    this.password = password;
    this.activateToken = activateToken;
    this.resetToken = resetToken;
  }

  public UserCredentials(
      TenantId tenantId,
      UserId userId,
      boolean enabled,
      String password,
      String activateToken,
      String resetToken) {
    super(null, tenantId);
    this.userId = userId;
    this.enabled = enabled;
    this.password = password;
    this.activateToken = activateToken;
    this.resetToken = resetToken;
  }
}

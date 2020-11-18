package wx.domain.admin;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.PermissionSettingId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermissionSetting extends IdBasedEntity<PermissionSettingId, PermissionSetting> {

  private String permissionName;

  private ApplicationId applicationId;

  private String directory;

  public PermissionSetting(
      PermissionSettingId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String permissionName,
      ApplicationId applicationId,
      String directory) {
    super(id, tenantId, createdAt, updatedAt);
    this.permissionName = permissionName;
    this.applicationId = applicationId;
    this.directory = directory;
  }

  public PermissionSetting(
      PermissionSettingId id,
      TenantId tenantId,
      String permissionName,
      ApplicationId applicationId,
      String directory) {
    super(id, tenantId);
    this.permissionName = permissionName;
    this.applicationId = applicationId;
    this.directory = directory;
  }
}

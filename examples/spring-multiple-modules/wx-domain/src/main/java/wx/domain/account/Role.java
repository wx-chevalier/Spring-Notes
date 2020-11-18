package wx.domain.account;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;

@Data
@EqualsAndHashCode(of = {"name", "tenantId"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

  private RoleId id;

  private String name;

  private TenantId tenantId;

  private UserId creatorId;

  private String nickname;

  private boolean disabled;

  public Role(RoleId roleId, String name, TenantId tenantId, String nickname, boolean disabled) {
    this(name, tenantId);
    this.id = roleId;
    this.nickname = nickname;
    this.disabled = disabled;
  }

  public Role(String name, TenantId tenantId, String nickname, boolean disabled) {
    this(name, tenantId);
    this.nickname = nickname;
    this.disabled = disabled;
  }

  public Role(String name, TenantId tenantId) {
    this.name = name;
    this.tenantId = tenantId;
  }
}

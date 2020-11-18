package wx.domain.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.shared.id.*;

@Data
@EqualsAndHashCode(of = {"nickname", "tenantId"})
public class Permission {

  private TenantId tenantId;

  private String nickname;

  private String name;

  private boolean disabled;

  public Permission(TenantId tenantId, String nickname, String name, boolean disabled) {
    this(tenantId, name);
    this.nickname = nickname;
    this.disabled = disabled;
  }

  public Permission(TenantId tenantId, String name) {
    this.tenantId = tenantId;
    this.name = name;
  }
}

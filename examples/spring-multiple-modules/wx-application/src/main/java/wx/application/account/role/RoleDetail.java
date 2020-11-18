package wx.application.account.role;

import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import wx.domain.account.Permission;
import wx.domain.account.Role;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDetail extends Role {

  private Collection<Permission> permissions;

  public RoleDetail(Role role, Collection<Permission> permissions) {
    super();
    BeanUtils.copyProperties(role, this);
    this.permissions = permissions;
  }
}

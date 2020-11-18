package wx.application.account.role;

import org.springframework.stereotype.Component;
import wx.common.data.account.RoleStatusEnum;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.RoleId;
import wx.domain.account.Role;
import wx.infra.tunnel.db.account.RoleDO;

@Component
public class RoleConverter extends AbstractConverter<Role, RoleDO> {

  @Override
  public RoleDO convertTo(Role role) {
    return new RoleDO()
        .setId(convertNullable(role.getId(), BaseEntityId::getId))
        .setName(role.getName())
        .setTenantId(role.getTenantId().getId())
        .setNickname(role.getNickname())
        .setStatus(role.isDisabled() ? RoleStatusEnum.DISABLE : RoleStatusEnum.ENABLE);
  }

  @Override
  public Role convertFrom(RoleDO roleDO) {
    return new Role(
        convertNullable(roleDO.getId(), RoleId::new),
        roleDO.getName(),
        new TenantId(roleDO.getTenantId()),
        roleDO.getNickname(),
        roleDO.getStatus() == RoleStatusEnum.DISABLE);
  }
}

package wx.application.infra.message;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.BaseEntityId;
import wx.domain.infra.message.RoleMessageConfig;
import wx.infra.tunnel.db.infra.message.RoleMessageConfigDO;

@Component
public class RoleMessageConfigConverter
    extends AbstractConverter<RoleMessageConfig, RoleMessageConfigDO> {

  @Override
  public RoleMessageConfigDO convertTo(RoleMessageConfig config) {
    return new RoleMessageConfigDO()
        .setRoleId(convertNullable(config.getRoleId(), BaseEntityId::getId))
        .setCreatorId(convertNullable(config.getCreatorId(), BaseEntityId::getId));
  }

  @Override
  public RoleMessageConfig convertFrom(RoleMessageConfigDO roleMessageConfigDO) {
    return super.convertFrom(roleMessageConfigDO);
  }
}

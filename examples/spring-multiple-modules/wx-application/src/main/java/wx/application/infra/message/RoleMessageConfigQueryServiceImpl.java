package wx.application.infra.message;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.shared.id.*;
import wx.infra.tunnel.db.infra.message.RoleMessageConfigTunnel;

@Slf4j
@Component
public class RoleMessageConfigQueryServiceImpl implements RoleMessageConfigQueryService {

  private RoleMessageConfigTunnel roleMessageConfigTunnel;

  public RoleMessageConfigQueryServiceImpl(RoleMessageConfigTunnel roleMessageConfigTunnel) {
    this.roleMessageConfigTunnel = roleMessageConfigTunnel;
  }

  @Override
  @Transactional(readOnly = true)
  public List<NoticeSendChannel> getSendChannelByTenantId(
      TenantId tenantId, MessageTypeId messageTypeId) {
    return roleMessageConfigTunnel.getRoleConfigByTenantId(tenantId, messageTypeId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserId> getTargetUserIds(TenantId tenantId, MessageTypeId messageTypeId) {
    return roleMessageConfigTunnel.getTargetUserIds(tenantId, messageTypeId);
  }
}

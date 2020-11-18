package wx.application.infra.message;

import java.util.List;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.shared.id.*;

public interface RoleMessageConfigQueryService {

  /** 获取指定用户的消息发送渠道 */
  List<NoticeSendChannel> getSendChannelByTenantId(TenantId tenantId, MessageTypeId messageTypeId);

  /** 获取某个租户下允许发送的messageTypeId 消息的用户集合 */
  List<UserId> getTargetUserIds(TenantId tenantId, MessageTypeId messageTypeId);
}

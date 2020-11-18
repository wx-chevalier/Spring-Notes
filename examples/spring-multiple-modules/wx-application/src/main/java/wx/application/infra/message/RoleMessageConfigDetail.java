package wx.application.infra.message;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleMessageConfigId;
import wx.domain.infra.message.MessageType;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleMessageConfigDetail
    extends IdBasedEntity<RoleMessageConfigId, RoleMessageConfigDetail> {

  private Collection<MessageType> messageTypes;

  private Collection<NoticeSendChannel> sendChannels;

  private Integer sendInterval;

  public RoleMessageConfigDetail(
      TenantId tenantId,
      Collection<MessageType> messageTypes,
      Collection<NoticeSendChannel> sendChannels,
      Integer sendInterval) {
    super(tenantId);
    this.messageTypes = messageTypes;
    this.sendChannels = sendChannels;
    this.sendInterval = sendInterval;
  }
}

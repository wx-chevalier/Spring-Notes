package wx.domain.infra.message;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.MessageTypeId;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.RoleMessageConfigId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleMessageConfig extends IdBasedEntity<RoleMessageConfigId, RoleMessageConfig> {

  private List<MessageTypeId> messageTypeId;

  private List<NoticeSendChannel> sendChannel;

  private NoticeTypeKind kind;

  private Integer sendInterval;

  private UserId creatorId;

  private RoleId roleId;

  public RoleMessageConfig(
      List<MessageTypeId> messageTypeId,
      List<NoticeSendChannel> sendChannel,
      Integer sendInterval) {
    super();
    this.messageTypeId = messageTypeId;
    this.sendChannel = sendChannel;
    this.sendInterval = sendInterval;
  }
}

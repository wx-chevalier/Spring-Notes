package wx.infra.tunnel.db.infra.message;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.EntityType;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_message_notice")
public class MessageNoticeDO extends BaseDO<MessageNoticeDO> {

  private NoticeTypeKind kind;

  private NoticeType type;

  private NoticeSendChannel sendChannel;

  private String content;

  private String title;

  private Long entityId;

  private EntityType entityType;

  private Boolean isRead;

  private Long userId;

  private Long appId;
}

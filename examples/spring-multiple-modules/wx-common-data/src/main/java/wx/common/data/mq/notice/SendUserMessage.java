package wx.common.data.mq.notice;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.UserId;

/** 发送通知的消息 */
@Data
public class SendUserMessage implements Serializable {

  private static final long serialVersionUID = 5675977320010356264L;

  /** 用户唯一标识 */
  private UserId userId;

  /** 发送目标 */
  private String dest;

  /** 消息类型、模板 */
  private NoticeType noticeType;

  /** 发送渠道 */
  private NoticeSendChannel channel;

  /** 消息参数 */
  private Map<String, String> param;

  /** 关联的资源ID */
  private BaseEntityId baseEntityId;
}

package wx.common.data.mq.notice;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;

/** 发送通知的消息 */
@Data
public class SendNoticeMessage implements Serializable {

  private static final long serialVersionUID = 5675977320010356264L;

  /** 租户域 */
  private TenantId tenantId;

  /** 消息类型、模板 */
  private NoticeType noticeType;

  /** 关联的资源ID */
  private BaseEntityId baseEntityId;

  private Map<String, String> param;
}

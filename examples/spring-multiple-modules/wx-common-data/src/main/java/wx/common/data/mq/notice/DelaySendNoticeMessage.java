package wx.common.data.mq.notice;

import java.io.Serializable;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;

/** 延时发送通知消息 */
@Data
public class DelaySendNoticeMessage implements Serializable {

  private static final long serialVersionUID = 1749153040575268763L;

  /** 消息已发送次数，此消息用于限制循环消息的发送上限 */
  @Setter(value = AccessLevel.NONE)
  private int sendCount = 1;

  /** 租户域 */
  private TenantId tenantId;

  /** 消息类型、模板 */
  private NoticeType noticeType;

  /** 关联的资源ID */
  private BaseEntityId baseEntityId;

  private Map<String, Object> param;

  /** 消息发送次数增加 */
  public void incrementSendCount() {
    this.sendCount++;
  }
}

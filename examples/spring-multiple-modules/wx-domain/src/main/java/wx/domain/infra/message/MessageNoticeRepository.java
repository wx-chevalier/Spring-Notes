package wx.domain.infra.message;

import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.MessageNoticeId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntityRepository;

public interface MessageNoticeRepository
    extends IdBasedEntityRepository<MessageNoticeId, MessageNotice> {

  /** 检查消息是够已存在 */
  boolean exists(
      NoticeType noticeType, NoticeSendChannel channel, BaseEntityId entityId, UserId userId);
}

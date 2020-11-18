package wx.application.infra.message;

import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.BaseEntityId;

/** 消息通知服务 */
public interface MessageNoticeQueryService {

  boolean isSendMsgAboutWorkOrder(BaseEntityId baseEntityId, NoticeType noticeType);
}

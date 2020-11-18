package wx.application.infra.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.BaseEntityId;
import wx.infra.tunnel.db.infra.message.MessageNoticeTunnel;

@Slf4j
@Service
public class MessageNoticeQueryServiceImpl implements MessageNoticeQueryService {

  private MessageNoticeTunnel messageNoticeTunnel;

  public MessageNoticeQueryServiceImpl(MessageNoticeTunnel messageNoticeTunnel) {
    this.messageNoticeTunnel = messageNoticeTunnel;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isSendMsgAboutWorkOrder(BaseEntityId entityId, NoticeType noticeType) {
    int count = messageNoticeTunnel.count(entityId, noticeType);
    return count != 0;
  }
}

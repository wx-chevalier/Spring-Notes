package wx.application.infra.message;

import java.util.Map;
import javax.validation.constraints.NotNull;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.mq.notice.SendNoticeMessage;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.UserId;

public interface NoticeMessageHandleService {

  void handle(@NotNull SendNoticeMessage msg);

  void send(
      NoticeSendChannel channel,
      UserId userId,
      NoticeType type,
      BaseEntityId entityId,
      Map<String, String> param);

  void send(
      NoticeSendChannel channel,
      String dest,
      NoticeType type,
      BaseEntityId entityId,
      Map<String, String> param);
}

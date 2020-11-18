package wx.domain.infra.message;

import java.util.Optional;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.MessageTypeId;
import wx.domain.shared.IdBasedEntityRepository;

public interface MessageTypeRepository extends IdBasedEntityRepository<MessageTypeId, MessageType> {

  Optional<MessageType> getByNoticeType(NoticeType type);
}

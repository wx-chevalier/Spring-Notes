package wx.application.infra.notice.site;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.domain.infra.message.MessageNotice;
import wx.infra.tunnel.db.infra.message.MessageNoticeDO;

@Component
public class SiteMessageConverter extends AbstractConverter<MessageNotice, MessageNoticeDO> {

  @Override
  public MessageNoticeDO convertTo(MessageNotice messageNotice) {
    return new MessageNoticeDO()
        .setId(convertNullable(messageNotice.getId(), BaseEntityId::getId))
        .setUserId(convertNullable(messageNotice.getUserId(), BaseEntityId::getId))
        .setAppId(messageNotice.getApp().getId())
        .setEntityId(convertNullable(messageNotice.getEntityId(), Long::valueOf))
        .setEntityType(messageNotice.getEntityType())
        .setKind(messageNotice.getKind())
        .setTitle(messageNotice.getTitle())
        .setSendChannel(messageNotice.getChannel());
  }

  @Override
  public MessageNotice convertFrom(MessageNoticeDO messageNoticeDO) {

    return new MessageNotice(
        messageNoticeDO.getKind(),
        messageNoticeDO.getType(),
        messageNoticeDO.getSendChannel(),
        convertNullable(messageNoticeDO.getAppId(), ApplicationId::new),
        messageNoticeDO.getContent(),
        messageNoticeDO.getTitle(),
        convertNullable(messageNoticeDO.getEntityId(), String::valueOf),
        messageNoticeDO.getEntityType(),
        convertNullable(messageNoticeDO.getUserId(), UserId::new),
        convertNullable(messageNoticeDO.getId(), MessageNoticeId::new),
        convertNullable(messageNoticeDO.getTenantId(), TenantId::new),
        messageNoticeDO.getIsRead(),
        messageNoticeDO.getCreatedAt(),
        messageNoticeDO.getUpdatedAt());
  }
}

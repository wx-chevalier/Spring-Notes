package wx.application.infra.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.infra.message.MessageTemplate;
import wx.common.data.infra.message.MessageTemplateSerializer;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.MessageTypeId;
import wx.domain.infra.message.MessageType;
import wx.infra.tunnel.db.infra.message.MessageTypeDO;

@Slf4j
@Component
public class MessageTypeConverter extends AbstractConverter<MessageType, MessageTypeDO> {

  @Override
  public MessageTypeDO convertTo(MessageType messageType) {

    MessageTemplateSerializer instance = MessageTemplateSerializer.instance();
    String template = convertNullable(messageType.getTemplate(), instance::toJson);

    return new MessageTypeDO()
        .setId(convertNullable(messageType.getId(), BaseEntityId::getId))
        .setKey(convertNullable(messageType.getKey(), NoticeType::name))
        .setName(messageType.getName())
        .setKind(messageType.getKind().name())
        .setTenantId(convertNullable(messageType.getTenantId(), BaseEntityId::getId))
        .setTemplate(template);
  }

  @Override
  public MessageType convertFrom(MessageTypeDO messageTypeDO) {

    // 构造模板
    MessageTemplate messageTemplate = new MessageTemplate();

    messageTemplate
        .setEmailTemplate(messageTypeDO.getEmailTemplate())
        .setSiteMessageTemplate(messageTypeDO.getSiteMessageTemplate())
        .setSmsTemplateId(messageTypeDO.getSmsTemplateCode())
        .setWechatTemplateId(messageTypeDO.getWechatTemplateCode());

    return new MessageType(
        convertNullable(messageTypeDO.getId(), MessageTypeId::new),
        convertNullable(messageTypeDO.getKey(), NoticeType::valueOf),
        convertNullable(messageTypeDO.getTenantId(), TenantId::new),
        null,
        messageTypeDO.getCreatedAt(),
        messageTypeDO.getUpdatedAt(),
        messageTypeDO.getName(),
        messageTypeDO.getKind(),
        messageTemplate);
  }
}

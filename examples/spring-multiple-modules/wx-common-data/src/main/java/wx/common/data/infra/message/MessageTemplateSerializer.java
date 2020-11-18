package wx.common.data.infra.message;

import wx.common.data.common.AbstractJsonSerializer;

public class MessageTemplateSerializer extends AbstractJsonSerializer<MessageTemplate> {

  private static MessageTemplateSerializer serializer;

  public static synchronized MessageTemplateSerializer instance() {
    if (serializer == null) {
      serializer = new MessageTemplateSerializer();
    }
    return serializer;
  }

  private MessageTemplateSerializer() {
    super(MessageTemplate.class);
  }

  @Override
  public MessageTemplate fromJson(String jsonString) {
    return super.fromJson(jsonString);
  }

  @Override
  public String toJson(MessageTemplate messageTemplate) {
    return super.toJson(messageTemplate);
  }
}

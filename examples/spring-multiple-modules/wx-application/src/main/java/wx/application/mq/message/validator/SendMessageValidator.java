package wx.application.mq.message.validator;

import wx.common.data.shared.id.BaseEntityId;

public interface SendMessageValidator {
  /**
   * 校验
   *
   * @param baseEntityId 资源ID
   */
  boolean validate(BaseEntityId baseEntityId);
}

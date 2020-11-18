package wx.application.mq.message.validator;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.application.mq.message.validator.impl.DeviceInitErrorValidatorImpl;
import wx.common.data.infra.notice.NoticeType;

@Slf4j
@Component
public class SendMessageValidatorMap {

  private static Map<NoticeType, SendMessageValidator> instance = null;

  static {
    instance = new HashMap<>();
    instance.put(NoticeType.DEVICE_OFFLINE, new DeviceInitErrorValidatorImpl());
    instance.put(NoticeType.DEVICE_EXPIRE, new DeviceInitErrorValidatorImpl());
    instance.put(NoticeType.DEVICE_RECONNECT, new DeviceInitErrorValidatorImpl());
  }

  public static SendMessageValidator get(NoticeType key) {
    if (instance == null) {
      return null;
    } else return instance.get(key);
  }
}

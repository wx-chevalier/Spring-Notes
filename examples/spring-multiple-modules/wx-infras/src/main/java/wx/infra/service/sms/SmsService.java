package wx.infra.service.sms;

import java.util.Map;

public interface SmsService {

  /**
   * 发送阿里云短信服务
   *
   * @param phoneNumber 发送的手机号码
   * @param templateId 消息模板ID
   * @param param 参数信息
   */
  void send(String phoneNumber, String templateId, Map<String, String> param);
}

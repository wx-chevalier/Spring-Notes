package wx.application.infra.notice.sms;

import wx.application.infra.notice.NoticeService;

public interface SmsService extends NoticeService {

  int SMS_EFFECTIVE_TIME = 10;

  /** 发送登录验证码 */
  void sendLoginCode(String phoneNumber);
}

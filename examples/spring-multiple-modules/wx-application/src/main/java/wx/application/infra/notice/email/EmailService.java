package wx.application.infra.notice.email;

import wx.application.infra.notice.NoticeService;

public interface EmailService extends NoticeService {

  /** 邮件的有效期 (单位: 分钟) */
  int EMAIL_EFFECTIVE_TIME = 30;

  /** 发送邮件内容 */
  void send(String targetDst, String subject, String template);

  /** 发送验证码登录邮件 */
  void sendLoginCode(String sendDst);
}

package wx.infra.service.email;

public interface EmailService {

  /** 发送邮件服务 */
  void send(String emailDst, String subject, String content);
}

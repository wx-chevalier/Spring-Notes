package wx.infra.service.email;

import com.sun.mail.util.MailSSLSocketFactory;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service("emailService")
public class EmailServiceImpl implements EmailService {

  private EmailSetting emailSetting;

  public EmailServiceImpl(EmailSetting emailSetting) {
    this.emailSetting = emailSetting;
  }

  @Override
  public void send(String emailDst, String subject, String content) {
    MimeMessage mimeMessage =
        new MimeMessage(
            Session.getDefaultInstance(
                properties(emailSetting.getHost(), emailSetting.getPort()),
                authenticator(emailSetting.getUsername(), emailSetting.getPassword())));
    try {

      // 准备发送消息
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setFrom(
          new InternetAddress(emailSetting.getUsername(), emailSetting.getPersonal(), "UTF-8"));
      helper.setSubject(subject);
      helper.setSentDate(new Date());
      helper.setTo(emailDst);
      helper.setText(content, true);
      mimeMessage.saveChanges();
      Transport.send(mimeMessage);
    } catch (Exception e) {
      log.error("发送邮件出现异常", e);
    }
  }

  // region Email 邮件发送配置区域
  private static Properties properties(String host, Integer port) {
    Properties prop = new Properties();

    prop.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory());
    prop.put("mail.smtp.ssl.enable", "true");
    prop.put("mail.transport.protocol", "smtp");
    prop.put("mail.smtp.host", host);
    prop.put("mail.smtp.port", Integer.toString(port));
    prop.put("mail.smtp.auth", Boolean.TRUE.toString());

    return prop;
  }

  private static Authenticator authenticator(String username, String password) {
    return new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    };
  }

  private static MailSSLSocketFactory mailSSLSocketFactory() {
    try {
      MailSSLSocketFactory sf = new MailSSLSocketFactory();
      sf.setTrustAllHosts(true);
      return sf;
    } catch (GeneralSecurityException e) {
      throw new EmailException(e);
    }
  }
  // endregion
}

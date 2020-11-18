package wx.api.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

@Component
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperties {
  private SecurityProperties security;

  private TimeSeriesProperties timeSeries;

  private MqttProperties mqtt;

  private FileStoreProperties fileStore;

  private OSSProperties oss;

  private SMSProperties sms;

  private CorsConfiguration cors = new CorsConfiguration();

  private MailProperties mail;

  private DingTalkProperties dingTalk;

  private WechatProperties wechat;
}

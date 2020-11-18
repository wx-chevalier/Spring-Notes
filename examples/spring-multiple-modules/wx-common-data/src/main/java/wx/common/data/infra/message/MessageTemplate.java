package wx.common.data.infra.message;

import lombok.Data;

@Data
public class MessageTemplate {

  /** 短消息模板ID （目前为阿里云短信服务） */
  private String smsTemplateId;

  /** 微信模板消息ID */
  private String wechatTemplateId;

  /** 电子邮箱模板(Html 源码，使用Freemarker 替换占位符) */
  private String emailTemplate;

  /** 站内信模板 */
  private String siteMessageTemplate;
}

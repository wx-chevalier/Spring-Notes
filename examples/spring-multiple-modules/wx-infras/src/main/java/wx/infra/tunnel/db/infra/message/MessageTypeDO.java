package wx.infra.tunnel.db.infra.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_message_type")
public class MessageTypeDO extends BaseDO<MessageTypeDO> {

  @TableField(keepGlobalFormat = true)
  private String key;

  private String name;

  private String kind;

  private Long appId;

  private String template;

  private String emailTemplate;

  private String smsTemplateCode;

  private String wechatTemplateCode;

  private String siteMessageTemplate;
}

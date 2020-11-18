package wx.common.data.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "xml")
public class WechatResponseTextMessage {

  @JsonProperty("ToUserName")
  private String toUserName;

  @JsonProperty("FromUserName")
  private String fromUserName;

  @JsonProperty("CreateTime")
  private String createTime;

  @JsonProperty("MsgType")
  private String msgType;

  @JsonProperty("Content")
  private String content;
}

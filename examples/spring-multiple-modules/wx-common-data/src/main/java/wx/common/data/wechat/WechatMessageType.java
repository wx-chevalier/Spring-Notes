package wx.common.data.wechat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum WechatMessageType {
  text("文本消息"),
  image("图片消息"),
  voice("语音消息"),
  shortvideo("短视频消息"),
  video("视频消息"),
  location("位置消息"),
  link("链接消息"),
  event("事件类型消息");

  @Getter private String desc;
}

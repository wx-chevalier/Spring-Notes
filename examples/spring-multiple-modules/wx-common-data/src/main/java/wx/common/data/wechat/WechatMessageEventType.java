package wx.common.data.wechat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum WechatMessageEventType {
  SCAN("扫描二维码"),
  subscribe("订阅事件"),
  unsubscribe("取消订阅事件");

  @Getter private String desc;
}

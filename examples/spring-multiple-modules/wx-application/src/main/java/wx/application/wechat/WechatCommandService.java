package wx.application.wechat;

import java.io.IOException;
import wx.common.data.wechat.WechatMessage;
import wx.common.data.wechat.WechatResponseTextMessage;

public interface WechatCommandService {

  /** 定时任务更新微信AccessToken */
  void updateAccessToken() throws IOException;

  /** 处理微信消息 */
  WechatResponseTextMessage handleMessage(WechatMessage message);
}

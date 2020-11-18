package wx.infra.common.data.wechat.req;

import lombok.Data;

@Data
public class WechatActionInfo {
  private WechatScene scene;

  public WechatActionInfo(WechatScene scene) {
    this.scene = scene;
  }
}

package wx.infra.common.data.wechat.req;

public enum WechatSceneEnum {
  LOGIN,
  BOUND;

  public static WechatSceneEnum getByName(String scene) {
    if (scene == null || scene.length() == 0) {
      return null;
    }
    return WechatSceneEnum.valueOf(scene);
  }
}

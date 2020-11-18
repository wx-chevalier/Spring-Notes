package wx.domain.wechat;

import java.util.Optional;
import wx.common.data.shared.id.UserId;

public interface WechatUserInfoRepository {

  Optional<WechatUser> getByOpenId(String openId);

  WechatUser save(WechatUser user);

  /** 获取用户绑定的信息 */
  Optional<WechatUser> getByUserId(UserId userId);
}

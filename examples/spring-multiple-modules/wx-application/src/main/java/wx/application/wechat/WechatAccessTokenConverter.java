package wx.application.wechat;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.domain.wechat.WechatAccessToken;
import wx.infra.tunnel.db.infra.wechat.WechatAccessTokenDO;

@Component
public class WechatAccessTokenConverter
    extends AbstractConverter<WechatAccessToken, WechatAccessTokenDO> {

  @Override
  public WechatAccessTokenDO convertTo(WechatAccessToken wechatAccessToken) {
    return new WechatAccessTokenDO()
        .setId(wechatAccessToken.getId())
        .setAccessToken(wechatAccessToken.getAccessToken())
        .setExpiresIn(wechatAccessToken.getExpiresIn())
        .setCreatedAt(wechatAccessToken.getCreatedAt())
        .setDeletedAt(wechatAccessToken.getDeletedAt());
  }

  @Override
  public WechatAccessToken convertFrom(WechatAccessTokenDO wechatAccessTokenDO) {
    return new WechatAccessToken()
        .setId(wechatAccessTokenDO.getId())
        .setAccessToken(wechatAccessTokenDO.getAccessToken())
        .setExpiresIn(wechatAccessTokenDO.getExpiresIn())
        .setCreatedAt(wechatAccessTokenDO.getCreatedAt())
        .setDeletedAt(wechatAccessTokenDO.getDeletedAt());
  }
}

package wx.application.wechat;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.wechat.WechatUser;
import wx.infra.common.util.DateTimeUtils;
import wx.infra.tunnel.db.infra.wechat.WechatUserDO;

@Component
public class WechatUserConverter extends AbstractConverter<WechatUser, WechatUserDO> {

  @Override
  public WechatUserDO convertTo(WechatUser user) {
    return new WechatUserDO()
        .setId(user.getId())
        .setUserId(convertNullable(user.getUserId(), UserId::getId))
        .setCity(user.getCity())
        .setCountry(user.getCountry())
        .setProvince(user.getProvince())
        .setHeadImgUrl(user.getHeadImgUrl())
        .setNickName(user.getNickName())
        .setLanguage(user.getLanguage())
        .setOpenId(user.getOpenId())
        .setSex(user.getSex())
        .setSubscribeScene(user.getSubscribeScene())
        .setUnsubscribeTime(user.getUnsubscribeTime())
        .setSubscribeTime(user.getSubscribeTime())
        .setUnionId(user.getUnionId())
        .setTenantId(convertNullable(user.getTenantId(), TenantId::getId));
  }

  @Override
  public WechatUser convertFrom(WechatUserDO userDO) {
    return new WechatUser()
        .setId(userDO.getId())
        .setUserId(convertNullable(userDO.getUserId(), UserId::new))
        .setCity(userDO.getCity())
        .setCountry(userDO.getCountry())
        .setProvince(userDO.getProvince())
        .setHeadImgUrl(userDO.getHeadImgUrl())
        .setNickName(userDO.getNickName())
        .setLanguage(userDO.getLanguage())
        .setOpenId(userDO.getOpenId())
        .setSex(userDO.getSex())
        .setSubscribeScene(userDO.getSubscribeScene())
        .setUnsubscribeTime(
            convertNullable(userDO.getUnsubscribeTime(), DateTimeUtils::toTimestamp))
        .setSubscribeTime(convertNullable(userDO.getSubscribeTime(), DateTimeUtils::toTimestamp))
        .setUnionId(userDO.getUnionId())
        .setTenantId(convertNullable(userDO.getTenantId(), TenantId::new));
  }
}

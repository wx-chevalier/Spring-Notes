package wx.infra.tunnel.db.infra.wechat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.wechat.WechatUserMapper;

@Component
public class WechatUserTunnel extends ServiceImpl<WechatUserMapper, WechatUserDO> {

  public WechatUserDO getByOpenId(String openId) {
    Wrapper<WechatUserDO> queryWrapper =
        Helper.getQueryWrapper(WechatUserDO.class)
            .eq(WechatUserDO::getOpenId, openId)
            .isNull(WechatUserDO::getUnsubscribeTime)
            .orderByDesc(WechatUserDO::getId)
            .last("LIMIT 1");
    return this.getOne(queryWrapper);
  }

  public WechatUserDO getByUserId(Long userId) {
    Wrapper<WechatUserDO> queryWrapper =
        Helper.getQueryWrapper(WechatUserDO.class)
            .eq(WechatUserDO::getUserId, userId)
            .isNull(WechatUserDO::getUnsubscribeTime)
            .orderByDesc(WechatUserDO::getId)
            .last("LIMIT 1");
    return this.getOne(queryWrapper);
  }
}

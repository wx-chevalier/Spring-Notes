package wx.infra.tunnel.db.infra.wechat;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.mapper.infra.wechat.WechatAccessTokenMapper;

@Component
public class WechatAccessTokenTunnel
    extends ServiceImpl<WechatAccessTokenMapper, WechatAccessTokenDO> {}

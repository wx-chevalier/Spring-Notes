package wx.infra.tunnel.db.mapper.infra.wechat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.infra.wechat.WechatAccessTokenDO;

@Component
public interface WechatAccessTokenMapper extends BaseMapper<WechatAccessTokenDO> {}

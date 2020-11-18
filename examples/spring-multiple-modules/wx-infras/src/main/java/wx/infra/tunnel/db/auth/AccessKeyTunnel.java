package wx.infra.tunnel.db.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.mapper.auth.AccessKeyMapper;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccessKeyTunnel extends ServiceImpl<AccessKeyMapper, AccessKeyDO> {

  public boolean exists(String key) {
    return count(new LambdaQueryWrapper<AccessKeyDO>().eq(AccessKeyDO::getKey, key)) != 0;
  }
}

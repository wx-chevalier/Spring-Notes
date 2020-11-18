package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.account.TenantDO;

@Component
public interface TenantMapper extends BaseMapper<TenantDO> {}

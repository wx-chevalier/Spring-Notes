package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.account.UserTenantRelationDO;

@Component
public interface UserTenantRelationMapper extends BaseMapper<UserTenantRelationDO> {}

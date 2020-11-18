package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.account.UserConfigStatusDO;

/** 用户配置记录表状态 */
@Component
public interface UserConfigStatusMapper extends BaseMapper<UserConfigStatusDO> {}

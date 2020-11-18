package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import wx.common.data.account.Authority;
import wx.infra.tunnel.db.account.UserDO;

@Component
public interface UserMapper extends BaseMapper<UserDO> {

  IPage<UserDO> find(
      IPage<UserDO> page,
      @Param("tenantId") Long tenantId,
      @Param("authority") Authority authority,
      @Param("roleId") Long roleId,
      @Param("key") String key);
}

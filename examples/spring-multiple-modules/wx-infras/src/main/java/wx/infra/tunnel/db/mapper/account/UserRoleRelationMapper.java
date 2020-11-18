package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.account.RoleDO;
import wx.infra.tunnel.db.account.UserRoleRelationDO;

@Component
public interface UserRoleRelationMapper extends BaseMapper<UserRoleRelationDO> {

  List<RoleDO> getRoleByUserId(@Param("userId") Long userId);
}

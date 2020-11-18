package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import wx.common.data.account.PermissionStatusEnum;
import wx.infra.tunnel.db.account.AppPermissionDO;
import wx.infra.tunnel.db.account.PermissionDO;
import wx.infra.tunnel.db.account.RolePermissionRelationDO;

@Component
public interface RolePermissionRelationMapper extends BaseMapper<RolePermissionRelationDO> {

  /** 获取指定角色的可用权限列表 */
  List<PermissionDO> getPermissionByRoleId(
      @Param("tenantId") Long tenantId,
      @Param("roleId") Long roleId,
      @Param("status") PermissionStatusEnum permissionStatusEnum);

  /** 查询给定角色的所有权限 */
  List<PermissionDO> getPermissionByRoleIds(@Param("roleIds") Set<Long> roleId);

  /** 查询角色的权限信息 */
  List<AppPermissionDO> findByRoleIds(
      @Param("tenantId") Long tenantId, @Param("roleIds") Set<Long> roleIds);
}

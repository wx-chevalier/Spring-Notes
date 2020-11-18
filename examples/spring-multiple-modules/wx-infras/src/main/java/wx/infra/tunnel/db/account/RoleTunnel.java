package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import wx.common.data.account.RoleStatusEnum;
import wx.common.data.shared.id.*;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.RoleMapper;

@Component
public class RoleTunnel extends ServiceImpl<RoleMapper, RoleDO> {
  public List<RoleDO> findByNames(Collection<String> roleNames) {
    if (roleNames.size() == 0) {
      return new ArrayList<>();
    }
    return list(Helper.roleQ().in(RoleDO::getName, roleNames));
  }

  /**
   * 查询某个租户下的角色列表
   *
   * @param tenantId 租户ID
   * @param status 状态 (若该状态为NULL，则查询全部状态的角色信息)
   * @return 查询到的所有的数据信息
   */
  public List<RoleDO> getByTenantId(TenantId tenantId, RoleStatusEnum status) {
    Wrapper<RoleDO> queryWrapper =
        Helper.roleQ()
            .eq(RoleDO::getTenantId, tenantId.getId())
            .eq(Objects.nonNull(status), RoleDO::getStatus, status)
            .orderByDesc(RoleDO::getId);
    return this.baseMapper.selectList(queryWrapper);
  }

  public RoleDO addRole(RoleDO RoleDO) {
    this.baseMapper.insert(RoleDO);
    return this.baseMapper.selectById(RoleDO.getId());
  }

  public Boolean checkExistWithName(TenantId tenantId, String roleName) {
    Wrapper<RoleDO> queryWrapper =
        Helper.roleQ()
            .eq(RoleDO::getName, roleName)
            .eq(RoleDO::getTenantId, tenantId.getId())
            .eq(RoleDO::getStatus, RoleStatusEnum.ENABLE);
    return this.baseMapper.selectCount(queryWrapper) > 0;
  }

  /**
   * 更新DO 对象，并返回更新后的新实体对象
   *
   * @param roleDO 更新的DO 对象
   * @return 返回更新后的实体对象信息
   */
  public RoleDO updateAndReturnById(RoleDO roleDO) {
    this.updateById(roleDO);
    return this.getById(roleDO.getId());
  }

  /** 检查租户下是否存在角色 */
  public boolean exist(TenantId tenantId, Long roleId) {
    Wrapper<RoleDO> queryWrapper =
        Helper.getQueryWrapper(RoleDO.class)
            .eq(RoleDO::getTenantId, tenantId.getId())
            .eq(RoleDO::getId, roleId);
    return this.baseMapper.selectCount(queryWrapper) > 0;
  }
}

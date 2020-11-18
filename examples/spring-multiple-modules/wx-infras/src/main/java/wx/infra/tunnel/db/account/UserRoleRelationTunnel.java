package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.UserRoleRelationMapper;

@Component
public class UserRoleRelationTunnel
    extends ServiceImpl<UserRoleRelationMapper, UserRoleRelationDO> {
  private RoleTunnel roleTunnel;

  public UserRoleRelationTunnel(RoleTunnel roleTunnel) {
    this.roleTunnel = roleTunnel;
  }

  public List<RoleDO> listByUser(TenantId tenantId, UserId userId) {
    List<UserRoleRelationDO> userRoles =
        list(
            Helper.userRoleRelationQ()
                .eq(UserRoleRelationDO::getTenantId, tenantId.getId())
                .eq(UserRoleRelationDO::getUserId, userId.getId()));
    if (userRoles.size() == 0) {
      return new ArrayList<>();
    }
    return roleTunnel.list(
        Helper.roleQ()
            .in(
                RoleDO::getId,
                userRoles.stream()
                    .map(UserRoleRelationDO::getRoleId)
                    .collect(Collectors.toList())));
  }

  public List<RoleDO> getRoleInfoByUserIds(Long userIds) {
    return this.baseMapper.getRoleByUserId(userIds);
  }

  /**
   * 更新用户的角色权限
   *
   * @return
   */
  public boolean save(
      UserId userId, Collection<RoleId> userRoleIds, TenantId tenantId, UserId creatorId) {
    if (CollectionUtils.isEmpty(userRoleIds)) {
      return true;
    }
    // 清除原来的数据
    cleanByUserId(userId);

    // 新增元数据
    Set<Long> roleIds = userRoleIds.stream().map(BaseEntityId::getId).collect(Collectors.toSet());
    Collection<UserRoleRelationDO> relationDOS = new ArrayList<>(roleIds.size());
    for (Long roleId : roleIds) {
      UserRoleRelationDO relationDO = new UserRoleRelationDO();
      relationDO
          .setRoleId(roleId)
          .setUserId(userId.getId())
          .setCreatorId(creatorId.getId())
          .setCreatorId(creatorId.getId())
          .setIsDisabled(Boolean.FALSE)
          .setTenantId(tenantId.getId());
      relationDOS.add(relationDO);
    }
    return this.saveBatch(relationDOS);
  }

  private void cleanByUserId(UserId userId) {
    UserRoleRelationDO entity = new UserRoleRelationDO();
    entity.setDeletedAt(LocalDateTime.now());

    Wrapper<UserRoleRelationDO> updateWrapper =
        Helper.getUpdateWrapper(UserRoleRelationDO.class)
            .eq(UserRoleRelationDO::getUserId, userId.getId())
            .isNull(UserRoleRelationDO::getDeletedAt);
    this.baseMapper.update(entity, updateWrapper);
  }

  public void remove(TenantId tenantId, RoleId roleId) {
    Wrapper<UserRoleRelationDO> updateWrapper =
        Helper.getUpdateWrapper(UserRoleRelationDO.class)
            .eq(UserRoleRelationDO::getRoleId, roleId.getId())
            .eq(UserRoleRelationDO::getTenantId, tenantId.getId())
            .isNull(UserRoleRelationDO::getDeletedAt)
            .set(UserRoleRelationDO::getDeletedAt, LocalDateTime.now());
    this.update(updateWrapper);
  }

  /** 获取租户{@param tenantId}下某个角色{@param roleId} 的关联用户数目 */
  public int usageCount(TenantId tenantId, RoleId roleId) {
    Wrapper<UserRoleRelationDO> countWrapper =
        Helper.getUpdateWrapper(UserRoleRelationDO.class)
            .eq(UserRoleRelationDO::getRoleId, roleId.getId())
            .eq(UserRoleRelationDO::getTenantId, tenantId.getId())
            .isNull(UserRoleRelationDO::getDeletedAt);
    return this.count(countWrapper);
  }
}

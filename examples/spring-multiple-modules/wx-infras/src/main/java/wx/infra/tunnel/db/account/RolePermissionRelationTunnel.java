package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.common.data.account.PermissionStatusEnum;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.RolePermissionRelationMapper;

@Slf4j
@Component
public class RolePermissionRelationTunnel
    extends ServiceImpl<RolePermissionRelationMapper, RolePermissionRelationDO> {
  private PermissionTunnel permissionTunnel;

  public RolePermissionRelationTunnel(PermissionTunnel permissionTunnel) {
    this.permissionTunnel = permissionTunnel;
  }

  public List<PermissionDO> listPermissionByRoleIds(Collection<Long> roleIds) {
    if (roleIds.size() == 0) {
      return new ArrayList<>();
    }
    List<RolePermissionRelationDO> rolePermissionRelations =
        list(Helper.rolePermissionRelationQ().in(RolePermissionRelationDO::getRoleId, roleIds));
    return permissionTunnel.list(
        Helper.permissionQ()
            .in(
                PermissionDO::getId,
                rolePermissionRelations.stream()
                    .map(RolePermissionRelationDO::getPermissionId)
                    .collect(Collectors.toList())));
  }

  public void relationRolePermission(
      Long roleId, TenantId tenantId, UserId creatorId, Set<Long> ids) {
    if (!CollectionUtils.isEmpty(ids)) {
      log.warn("跳过更新角色权限关联操作,给定的权限列表为空,无法关联");
      return;
    }
    // 查询权限是否都存在
    Set<Long> notExistIds = permissionTunnel.checkAllExist(ids);
    if (!CollectionUtils.isEmpty(notExistIds)) {
      throw new NotFoundException(String.format("关联权限失败,权限[ids=%s] 不存在", notExistIds.toString()));
    }
    // 全部都存在，则进行更新操作
    List<RolePermissionRelationDO> entityList =
        ids.stream()
            .map(
                id ->
                    new RolePermissionRelationDO()
                        .setPermissionId(id)
                        .setRoleId(roleId)
                        .setCreatorId(creatorId.getId())
                        .setTenantId(tenantId.getId()))
            .collect(Collectors.toList());
    saveBatch(entityList);
  }

  public List<PermissionDO> getByRoleId(TenantId tenantId, Long roleId) {
    return this.baseMapper.getPermissionByRoleId(
        tenantId.getId(), roleId, PermissionStatusEnum.ENABLE);
  }

  /**
   * 移除某个角色下的权限列表
   *
   * @param tenantId 租户唯一标识信息
   * @param roleId 角色唯一标识
   * @param permissionIds 权限标识集合
   */
  public void removeByIdsAndRoleId(TenantId tenantId, Long roleId, Set<Long> permissionIds) {
    if (CollectionUtils.isEmpty(permissionIds)) {
      log.info("移除角色[id={}] 关联权限失败,权限列表为空", roleId);
      return;
    }
    Wrapper<RolePermissionRelationDO> deleteWrapper =
        Helper.getUpdateWrapper(RolePermissionRelationDO.class)
            .eq(RolePermissionRelationDO::getTenantId, tenantId.getId())
            .eq(RolePermissionRelationDO::getRoleId, roleId)
            .in(RolePermissionRelationDO::getPermissionId, permissionIds);
    this.baseMapper.delete(deleteWrapper);
  }

  public List<PermissionDO> getPermissionListByRoleIds(Set<Long> roleIds) {
    if (CollectionUtils.isEmpty(roleIds)) {
      log.info("给定的角色ID集合为空，跳过查询");
      return new ArrayList<>();
    }
    return this.baseMapper.getPermissionByRoleIds(roleIds);
  }

  public List<AppPermissionDO> findByRoleIds(TenantId tenantId, Set<RoleId> roleIds) {
    if (CollectionUtils.isEmpty(roleIds)) {
      return new ArrayList<>(0);
    }
    return this.baseMapper.findByRoleIds(
        tenantId.getId(), roleIds.stream().map(RoleId::getId).collect(Collectors.toSet()));
  }

  /** 删除某租户{@param tenantId}下的，和角色{@param roleId}关联的记录(逻辑删除) */
  public void remove(TenantId tenantId, RoleId roleId) {

    Wrapper<RolePermissionRelationDO> updateWrapper =
        Helper.getUpdateWrapper(RolePermissionRelationDO.class)
            .eq(RolePermissionRelationDO::getRoleId, roleId.getId())
            .eq(RolePermissionRelationDO::getTenantId, tenantId.getId())
            .isNull(RolePermissionRelationDO::getDeletedAt)
            .set(RolePermissionRelationDO::getDeletedAt, LocalDateTime.now());
    this.update(updateWrapper);
  }
}

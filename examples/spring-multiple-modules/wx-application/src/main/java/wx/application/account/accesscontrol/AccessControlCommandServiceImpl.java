package wx.application.account.accesscontrol;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.*;
import wx.infra.common.exception.NotAcceptException;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.account.*;
import wx.infra.tunnel.db.shared.BaseDO;

@Service
public class AccessControlCommandServiceImpl implements AccessControlCommandService {

  private UserRoleRelationTunnel userRoleRelationTunnel;
  private RolePermissionRelationTunnel rolePermissionRelationTunnel;
  private UserPermissionRelationTunnel userPermissionRelationTunnel;

  private RoleTunnel roleTunnel;
  private RoleRepository roleRepository;

  private PermissionTunnel permissionTunnel;
  private PermissionRepository permissionRepository;

  public AccessControlCommandServiceImpl(
      UserRoleRelationTunnel userRoleRelationTunnel,
      RolePermissionRelationTunnel rolePermissionRelationTunnel,
      UserPermissionRelationTunnel userPermissionRelationTunnel,
      RoleTunnel roleTunnel,
      RoleRepository roleRepository,
      PermissionTunnel permissionTunnel,
      PermissionRepository permissionRepository) {
    this.userRoleRelationTunnel = userRoleRelationTunnel;
    this.rolePermissionRelationTunnel = rolePermissionRelationTunnel;
    this.userPermissionRelationTunnel = userPermissionRelationTunnel;
    this.roleTunnel = roleTunnel;
    this.roleRepository = roleRepository;
    this.permissionTunnel = permissionTunnel;
    this.permissionRepository = permissionRepository;
  }

  @Override
  public Role addRole(Role role) {
    return roleRepository.saveOrUpdate(role);
  }

  @Override
  public Permission addPermission(TenantId tenantId, Permission permission) {
    return permissionRepository.saveOrUpdate(tenantId, permission);
  }

  @Override
  @Transactional
  public boolean addRolePermission(
      TenantId tenantId, UserId operatorId, Role role, Permission permission) {
    Long roleId = getRoleId(tenantId, role);
    Long permissionId = getPermissionId(permission);
    if (!isRolePermissionRelated(roleId, permissionId)) {
      return rolePermissionRelationTunnel.save(
          new RolePermissionRelationDO()
              .setRoleId(roleId)
              .setCreatorId(operatorId.getId())
              .setPermissionId(permissionId)
              .setTenantId(tenantId.getId()));
    }
    return false;
  }

  @Override
  @Transactional
  public boolean addRolePermissions(
      TenantId tenantId, UserId operatorId, Role role, List<Permission> permissions) {
    permissions
        .parallelStream()
        .forEach(permission -> addRolePermission(tenantId, operatorId, role, permission));
    return true;
  }

  @Override
  public boolean removeRolePermission(TenantId tenantId, Role role, Permission permission) {
    Long roleId = getRoleId(tenantId, role);
    Long permissionId = getPermissionId(permission);
    if (isRolePermissionRelated(roleId, permissionId)) {
      return rolePermissionRelationTunnel.remove(
          new LambdaQueryWrapper<RolePermissionRelationDO>()
              .eq(RolePermissionRelationDO::getTenantId, tenantId.getId())
              .eq(RolePermissionRelationDO::getRoleId, roleId)
              .eq(RolePermissionRelationDO::getPermissionId, permissionId));
    }
    return false;
  }

  @Override
  public boolean removeRolePermissions(TenantId tenantId, Role role, List<Permission> permissions) {
    permissions
        .parallelStream()
        .forEach(permission -> removeRolePermission(tenantId, role, permission));
    return true;
  }

  @Override
  public boolean addUserRole(TenantId tenantId, UserId userId, Role role) {
    Long roleId = getRoleId(tenantId, role);
    if (!isUserRoleRelated(userId, roleId)) {
      return userRoleRelationTunnel.save(
          new UserRoleRelationDO().setUserId(userId.getId()).setRoleId(roleId));
    }
    return false;
  }

  @Override
  public boolean addUserRole(
      TenantId tenantId, UserId userId, Collection<RoleId> roleIds, UserId creatorId) {
    return userRoleRelationTunnel.save(userId, roleIds, tenantId, creatorId);
  }

  @Override
  public boolean removeUserRole(TenantId tenantId, UserId userId, Role role) {
    Long roleId = getRoleId(tenantId, role);
    if (isUserRoleRelated(userId, roleId)) {
      return userRoleRelationTunnel.remove(
          new LambdaQueryWrapper<UserRoleRelationDO>()
              .eq(UserRoleRelationDO::getTenantId, tenantId.getId())
              .eq(UserRoleRelationDO::getUserId, userId.getId())
              .eq(UserRoleRelationDO::getRoleId, roleId));
    }
    return false;
  }

  @Override
  public boolean addUserPermission(TenantId tenantId, UserId userId, Permission permission) {
    Long permissionId = getPermissionId(permission);
    if (!isUserPermissionRelated(userId, permissionId)) {
      return userPermissionRelationTunnel.save(
          new UserPermissionRelationDO().setUserId(userId.getId()).setPermissionId(permissionId));
    }
    return false;
  }

  @Override
  public boolean removeUserPermission(TenantId tenantId, UserId userId, Permission permission) {
    Long permissionId = getPermissionId(permission);
    if (isUserPermissionRelated(userId, permissionId)) {
      return userPermissionRelationTunnel.remove(
          new LambdaQueryWrapper<UserPermissionRelationDO>()
              .eq(UserPermissionRelationDO::getTenantId, tenantId.getId())
              .eq(UserPermissionRelationDO::getUserId, userId.getId())
              .eq(UserPermissionRelationDO::getPermissionId, permissionId));
    }
    return false;
  }

  @Override
  @Transactional
  public void removeRole(TenantId tenantId, String roleName) {
    Role role =
        roleRepository
            .findByName(tenantId, roleName)
            .orElseThrow(() -> new NotFoundException("删除失败, 角色信息不存在"));

    // 检查角色是否正在使用
    int usageCount = userRoleRelationTunnel.usageCount(tenantId, role.getId());
    if (usageCount != 0) {
      throw new NotAcceptException("删除失败, 该角色仍然使用中");
    }

    // 删除角色
    roleRepository.remove(tenantId, roleName);

    // 删除用户-角色
    userRoleRelationTunnel.remove(tenantId, role.getId());

    // 删除角色-权限
    rolePermissionRelationTunnel.remove(tenantId, role.getId());
  }

  private boolean isUserPermissionRelated(UserId userId, Long permissionId) {
    return 0
        != userPermissionRelationTunnel.count(
            new LambdaQueryWrapper<UserPermissionRelationDO>()
                .eq(UserPermissionRelationDO::getUserId, userId.getId())
                .eq(UserPermissionRelationDO::getPermissionId, permissionId));
  }

  private boolean isUserRoleRelated(UserId userId, Long roleId) {
    return 0
        != userRoleRelationTunnel.count(
            new LambdaQueryWrapper<UserRoleRelationDO>()
                .eq(UserRoleRelationDO::getUserId, userId.getId())
                .eq(UserRoleRelationDO::getRoleId, roleId));
  }

  private boolean isRolePermissionRelated(Long roleId, Long permissionId) {
    return 0
        != rolePermissionRelationTunnel.count(
            new LambdaQueryWrapper<RolePermissionRelationDO>()
                .eq(RolePermissionRelationDO::getRoleId, roleId)
                .eq(RolePermissionRelationDO::getPermissionId, permissionId));
  }

  private Long getRoleId(TenantId tenantId, Role role) {
    return Optional.ofNullable(
            roleTunnel.getOne(
                new LambdaQueryWrapper<RoleDO>()
                    .eq(RoleDO::getTenantId, tenantId.getId())
                    .eq(RoleDO::getName, role.getName())))
        .map(RoleDO::getId)
        .orElseThrow(NotFoundException::new);
  }

  private Long getPermissionId(Permission permission) {
    return Optional.ofNullable(
            permissionTunnel.getOne(
                new LambdaQueryWrapper<PermissionDO>()
                    .eq(BaseDO::getTenantId, TenantId.NULL_TENANT_ID.getId())
                    .eq(PermissionDO::getName, permission.getName())))
        .map(PermissionDO::getId)
        .orElseThrow(NotFoundException::new);
  }
}

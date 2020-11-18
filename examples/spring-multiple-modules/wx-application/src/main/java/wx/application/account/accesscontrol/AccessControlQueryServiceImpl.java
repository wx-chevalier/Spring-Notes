package wx.application.account.accesscontrol;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.application.account.permission.PermissionConverter;
import wx.application.account.role.RoleConverter;
import wx.application.account.role.RoleDetail;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Permission;
import wx.domain.account.PermissionRepository;
import wx.domain.account.Role;
import wx.domain.account.RoleRepository;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.account.*;

@Service
public class AccessControlQueryServiceImpl implements AccessControlQueryService {

  private UserRoleRelationTunnel userRoleRelationTunnel;
  private RolePermissionRelationTunnel rolePermissionRelationTunnel;
  private UserPermissionRelationTunnel userPermissionRelationTunnel;
  private PermissionRepository permissionRepository;

  private RoleTunnel roleTunnel;
  private RoleRepository roleRepository;
  private RoleConverter roleConverter;
  private PermissionTunnel permissionTunnel;
  private PermissionConverter permissionConverter;

  public AccessControlQueryServiceImpl(
      UserRoleRelationTunnel userRoleRelationTunnel,
      RolePermissionRelationTunnel rolePermissionRelationTunnel,
      UserPermissionRelationTunnel userPermissionRelationTunnel,
      PermissionRepository permissionRepository,
      RoleTunnel roleTunnel,
      RoleRepository roleRepository,
      RoleConverter roleConverter,
      PermissionTunnel permissionTunnel,
      PermissionConverter permissionConverter) {
    this.userRoleRelationTunnel = userRoleRelationTunnel;
    this.rolePermissionRelationTunnel = rolePermissionRelationTunnel;
    this.userPermissionRelationTunnel = userPermissionRelationTunnel;
    this.permissionRepository = permissionRepository;
    this.roleTunnel = roleTunnel;
    this.roleRepository = roleRepository;
    this.roleConverter = roleConverter;
    this.permissionTunnel = permissionTunnel;
    this.permissionConverter = permissionConverter;
  }

  @Override
  @Transactional
  public List<RoleDetail> findRoles(TenantId tenantId) {
    List<Role> roles = roleRepository.findRoles(tenantId);
    return roles.stream()
        .map(
            role -> {
              List<Permission> permissions = findRolePermissions(tenantId, role);
              return new RoleDetail(role, permissions);
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<Role> findUserRoles(TenantId tenantId, UserId userId) {
    return userRoleRelationTunnel
        .list(
            new LambdaQueryWrapper<UserRoleRelationDO>()
                .eq(UserRoleRelationDO::getUserId, userId.getId()))
        .stream()
        .map(UserRoleRelationDO::getRoleId)
        .parallel()
        .map(roleTunnel::getById)
        .map(roleConverter::convertFrom)
        .collect(Collectors.toList());
  }

  private List<UserPermissionRelationDO> findDirectUserPermissions(
      TenantId tenantId, UserId userId) {
    return userPermissionRelationTunnel.list(
        new LambdaQueryWrapper<UserPermissionRelationDO>()
            .eq(UserPermissionRelationDO::getTenantId, tenantId.getId())
            .eq(UserPermissionRelationDO::getUserId, userId.getId()));
  }

  @Override
  public List<Permission> findUserPermissions(TenantId tenantId, UserId userId) {
    Map<String, Permission> userPermissions =
        new ConcurrentHashMap<>(
            findUserRoles(tenantId, userId)
                .parallelStream()
                .map(role -> findRolePermissions(tenantId, role))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Permission::getName, Function.identity())));
    findDirectUserPermissions(tenantId, userId)
        .parallelStream()
        .forEach(
            rel -> {
              String permissionName = getPermissionName(rel.getPermissionId());
              if (rel.getIsDisabled()) {
                // 移除直接禁用的权限
                userPermissions.remove(permissionName);
              } else {
                if (!userPermissions.containsKey(permissionName)) {
                  // 添加手工开启的权限
                  userPermissions.put(
                      permissionName,
                      permissionRepository
                          .findByName(tenantId, permissionName)
                          .orElseThrow(NotFoundException::new));
                }
              }
            });
    return new ArrayList<>(userPermissions.values());
  }

  @Override
  public List<Permission> findRolePermissions(TenantId tenantId, Role role) {
    RoleDO roleDO =
        roleTunnel.getOne(
            new LambdaQueryWrapper<RoleDO>()
                .eq(RoleDO::getTenantId, tenantId.getId())
                .eq(RoleDO::getName, role.getName()));
    if (roleDO == null) {
      return new ArrayList<>();
    }
    List<RolePermissionRelationDO> list =
        rolePermissionRelationTunnel.list(
            new LambdaQueryWrapper<RolePermissionRelationDO>()
                .eq(RolePermissionRelationDO::getRoleId, roleDO.getId())
                .orderByDesc(RolePermissionRelationDO::getId));
    return list.stream()
        .map(RolePermissionRelationDO::getPermissionId)
        .parallel()
        .map(permissionTunnel::getById)
        .map(permissionConverter::convertFrom)
        .collect(Collectors.toList());
  }

  private String getPermissionName(Long permissionId) {
    return Optional.ofNullable(permissionTunnel.getById(permissionId))
        .map(PermissionDO::getName)
        .orElseThrow(NotFoundException::new);
  }
}

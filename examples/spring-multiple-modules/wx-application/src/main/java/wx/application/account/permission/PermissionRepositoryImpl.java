package wx.application.account.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.shared.id.*;
import wx.domain.account.Permission;
import wx.domain.account.PermissionRepository;
import wx.infra.common.exception.DataValidationException;
import wx.infra.tunnel.db.account.PermissionDO;
import wx.infra.tunnel.db.account.PermissionTunnel;
import wx.infra.validator.CrudObjectValidator;

@Repository
public class PermissionRepositoryImpl implements PermissionRepository {

  private PermissionTunnel tunnel;
  private PermissionConverter converter;
  private CrudObjectValidator<Permission> validator =
      new CrudObjectValidator<Permission>() {
        @Override
        public void validateDataImpl(TenantId tenantId, Permission data) {
          if (data.getName() == null) {
            throw new DataValidationException("Permission name is null: " + data);
          }
        }

        @Override
        public void validateCreate(TenantId tenantId, Permission data) {
          validateDataImpl(tenantId, data);
        }

        @Override
        public void validateUpdate(TenantId tenantId, Permission data) {
          validateDataImpl(tenantId, data);
        }
      };

  public PermissionRepositoryImpl(PermissionTunnel tunnel, PermissionConverter converter) {
    this.tunnel = tunnel;
    this.converter = converter;
  }

  @Override
  public Permission saveOrUpdate(TenantId tenantId, Permission permission) {
    PermissionDO roleDO = converter.convertTo(permission);
    Optional<PermissionDO> found = getOneByName(tenantId, permission.getName());
    if (found.isPresent()) {
      roleDO.setId(found.get().getId());
      validator.validateUpdate(tenantId, permission);
      tunnel.updateById(roleDO);
    } else {
      validator.validateCreate(tenantId, permission);
      tunnel.save(roleDO);
    }
    return converter.convertFrom(roleDO);
  }

  @Override
  public boolean remove(TenantId tenantId, String name) {
    if (!getOneByName(tenantId, name).isPresent()) {
      return false;
    }
    return tunnel.update(
        new LambdaUpdateWrapper<PermissionDO>()
            .isNull(PermissionDO::getDeletedAt)
            .eq(PermissionDO::getTenantId, tenantId.getId())
            .eq(PermissionDO::getName, name)
            .set(PermissionDO::getDeletedAt, LocalDateTime.now()));
  }

  @Override
  public Optional<Permission> findByName(TenantId tenantId, String name) {
    return getOneByName(tenantId, name).map(converter::convertFrom);
  }

  @Override
  public List<Permission> findPermissions(TenantId tenantId) {
    return tunnel
        .list(
            new LambdaQueryWrapper<PermissionDO>().eq(PermissionDO::getTenantId, tenantId.getId()))
        .stream()
        .map(converter::convertFrom)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Permission> findByNames(Set<String> permissionNameSet) {
    List<PermissionDO> permissionDOList = tunnel.list(permissionNameSet);
    return permissionDOList.stream()
        .map(converter::convertFrom)
        .collect(Collectors.toMap(Permission::getName, Function.identity()));
  }

  private Optional<PermissionDO> getOneByName(TenantId tenantId, String name) {
    return Optional.ofNullable(
        tunnel.getOne(
            new LambdaQueryWrapper<PermissionDO>()
                .isNull(PermissionDO::getDeletedAt)
                .eq(PermissionDO::getTenantId, tenantId.getId())
                .eq(PermissionDO::getName, name)));
  }
}

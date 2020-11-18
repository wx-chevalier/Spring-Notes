package wx.application.account.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.domain.account.Role;
import wx.domain.account.RoleRepository;
import wx.infra.common.exception.DataValidationException;
import wx.infra.tunnel.db.account.RoleDO;
import wx.infra.tunnel.db.account.RoleTunnel;
import wx.infra.validator.CrudObjectValidator;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

  private RoleTunnel tunnel;
  private RoleConverter converter;
  private CrudObjectValidator<Role> validator =
      new CrudObjectValidator<Role>() {
        @Override
        public void validateDataImpl(TenantId tenantId, Role data) {
          if (data.getName() == null) {
            throw new DataValidationException("Role name is null: " + data);
          }
        }

        @Override
        public void validateCreate(TenantId tenantId, Role data) {
          validateDataImpl(tenantId, data);
        }

        @Override
        public void validateUpdate(TenantId tenantId, Role data) {
          validateDataImpl(tenantId, data);
        }
      };

  public RoleRepositoryImpl(RoleTunnel tunnel, RoleConverter converter) {
    this.tunnel = tunnel;
    this.converter = converter;
  }

  @Override
  @Transactional
  public Role saveOrUpdate(Role role) {
    TenantId tenantId = role.getTenantId();
    RoleDO roleDO = converter.convertTo(role);
    // 查询用户是否存在
    Optional<RoleDO> found = getOneByName(tenantId, role.getName());

    // 存在则更新，否则创建新的角色
    if (found.isPresent()) {
      roleDO.setId(found.get().getId());
      validator.validateUpdate(tenantId, role);
      tunnel.updateById(roleDO);
    } else {
      validator.validateCreate(tenantId, role);
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
        new LambdaUpdateWrapper<RoleDO>()
            .isNull(RoleDO::getDeletedAt)
            .eq(RoleDO::getTenantId, tenantId.getId())
            .eq(RoleDO::getName, name)
            .set(RoleDO::getDeletedAt, LocalDateTime.now()));
  }

  @Override
  public Optional<Role> findByName(TenantId tenantId, String name) {
    return getOneByName(tenantId, name).map(converter::convertFrom);
  }

  @Override
  public List<Role> findRoles(TenantId tenantId) {
    return tunnel
        .list(
            new LambdaQueryWrapper<RoleDO>()
                .eq(RoleDO::getTenantId, tenantId.getId())
                .orderByDesc(RoleDO::getId))
        .stream()
        .map(converter::convertFrom)
        .collect(Collectors.toList());
  }

  private Optional<RoleDO> getOneByName(TenantId tenantId, String name) {
    return Optional.ofNullable(
        tunnel.getOne(
            new LambdaQueryWrapper<RoleDO>()
                .isNull(RoleDO::getDeletedAt)
                .eq(RoleDO::getTenantId, tenantId.getId())
                .eq(RoleDO::getName, name)));
  }

  @Override
  @Transactional
  public boolean exist(TenantId tenantId, RoleId roleId) {
    return tunnel.exist(tenantId, roleId.getId());
  }
}

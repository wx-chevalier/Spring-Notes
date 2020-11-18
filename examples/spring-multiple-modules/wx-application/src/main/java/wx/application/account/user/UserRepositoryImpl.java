package wx.application.account.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import wx.application.account.accesscontrol.UserConverter;
import wx.common.data.account.Authority;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;
import wx.domain.account.UserRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.account.UserDO;
import wx.infra.tunnel.db.account.UserTunnel;
import wx.infra.tunnel.db.mapper.account.UserMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Repository
public class UserRepositoryImpl
    extends MyBatisIdBasedEntityRepository<UserTunnel, UserMapper, UserDO, User, UserId>
    implements UserRepository {

  @Getter(AccessLevel.PROTECTED)
  private UserConverter converter;

  public UserRepositoryImpl(UserTunnel userTunnel, UserMapper mapper, UserConverter converter) {
    super(userTunnel, mapper);
    this.converter = converter;
  }

  @Override
  public Optional<User> findById(UserId userId) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getId, userId.getId())
                        .isNull(UserDO::getDeletedAt)))
        .map(getConverter()::convertFrom);
  }

  /**
   * @param tenantId TenantId
   * @param username 可能为 username/phoneNumber/email
   */
  @Override
  public Optional<User> findByUsername(TenantId tenantId, String username) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getTenantId, tenantId.getId())
                        .eq(UserDO::getUsername, username)
                        .or()
                        .eq(UserDO::getEmail, username)
                        .or()
                        .eq(UserDO::getPhoneNumber, username)))
        .map(getConverter()::convertFrom);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getUsername, username)
                        .or()
                        .eq(UserDO::getEmail, username)
                        .or()
                        .eq(UserDO::getPhoneNumber, username)))
        .map(getConverter()::convertFrom);
  }

  @Override
  @Transactional
  public boolean exists(String username) {
    return getTunnel()
            .count(
                new LambdaQueryWrapper<UserDO>()
                    .eq(UserDO::getUsername, username)
                    .or()
                    .eq(UserDO::getEmail, username)
                    .or()
                    .eq(UserDO::getPhoneNumber, username))
        > 0;
  }

  @Override
  public Optional<User> findAdmin(TenantId tenantId) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getTenantId, tenantId.getId())
                        .eq(UserDO::getAuthority, Authority.TENANT_ADMIN)
                        .isNull(BaseDO::getDeletedAt)
                        .orderByDesc(UserDO::getId)
                        .last("LIMIT 1")))
        .map(getConverter()::convertFrom);
  }

  @Override
  public Optional<User> findByPhoneNumber(String phoneNumber) {
    if (!StringUtils.hasText(phoneNumber) || phoneNumber.length() < 11) {
      return Optional.empty();
    }

    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<UserDO>()
                        .like(UserDO::getPhoneNumber, phoneNumber)
                        .isNull(BaseDO::getDeletedAt)
                        .orderByDesc(UserDO::getId)
                        .last("LIMIT 1")))
        .map(getConverter()::convertFrom);
  }

  @Override
  @Transactional
  public List<User> findAll(TenantId tenantId) {
    List<UserDO> userDOList =
        getTunnel()
            .list(
                new LambdaQueryWrapper<UserDO>()
                    .eq(BaseDO::getTenantId, tenantId.getId())
                    .eq(UserDO::getAuthority, Authority.TENANT_USER)
                    .isNull(BaseDO::getDeletedAt));
    return userDOList.stream().map(converter::convertFrom).collect(Collectors.toList());
  }
}

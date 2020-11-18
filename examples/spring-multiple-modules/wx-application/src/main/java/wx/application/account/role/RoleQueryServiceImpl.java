package wx.application.account.role;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Role;
import wx.infra.tunnel.db.account.UserRoleRelationTunnel;

@Slf4j
@Component
public class RoleQueryServiceImpl implements RoleQueryService {

  private UserRoleRelationTunnel userRoleRelationTunnel;

  private RoleConverter roleConverter;

  public RoleQueryServiceImpl(
      UserRoleRelationTunnel userRoleRelationTunnel, RoleConverter roleConverter) {
    this.userRoleRelationTunnel = userRoleRelationTunnel;
    this.roleConverter = roleConverter;
  }

  @Override
  public Single<List<Role>> findRoleByUserId(TenantId tenantId, UserId userId) {
    log.info("find user [id={}] roles", userId.getId());
    return Single.defer(
        () ->
            Single.just(
                userRoleRelationTunnel.getRoleInfoByUserIds(userId.getId()).stream()
                    .map(roleDO -> roleConverter.convertFrom(roleDO))
                    .collect(Collectors.toList())));
  }
}

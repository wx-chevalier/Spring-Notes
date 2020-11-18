package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.UserPermissionRelationMapper;

@Component
public class UserPermissionRelationTunnel
    extends ServiceImpl<UserPermissionRelationMapper, UserPermissionRelationDO> {
  private PermissionTunnel permissionTunnel;

  public UserPermissionRelationTunnel(PermissionTunnel permissionTunnel) {
    this.permissionTunnel = permissionTunnel;
  }

  public List<PermissionDO> listByUser(TenantId tenantId, UserId userId) {
    List<UserPermissionRelationDO> userPermissions =
        list(
            Helper.userPermissionRelationQ()
                .eq(UserPermissionRelationDO::getTenantId, tenantId.getId())
                .eq(UserPermissionRelationDO::getUserId, userId.getId()));
    if (userPermissions.size() == 0) {
      return new ArrayList<>();
    }
    return permissionTunnel.list(
        Helper.permissionQ()
            .in(
                PermissionDO::getId,
                userPermissions.stream()
                    .map(UserPermissionRelationDO::getPermissionId)
                    .collect(Collectors.toList())));
  }
}

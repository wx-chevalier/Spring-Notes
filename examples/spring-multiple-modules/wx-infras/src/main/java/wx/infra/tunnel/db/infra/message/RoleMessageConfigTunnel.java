package wx.infra.tunnel.db.infra.message;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.shared.id.*;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.message.RoleMessageConfigMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
public class RoleMessageConfigTunnel
    extends ServiceImpl<RoleMessageConfigMapper, RoleMessageConfigDO> {

  /** 获取指定租户下，某角色的消息推送配置信息 */
  public List<RoleMessageConfigDO> getByRoleId(TenantId tenantId, RoleId roleId) {
    Wrapper<RoleMessageConfigDO> queryWrapper =
        Helper.getQueryWrapper(RoleMessageConfigDO.class)
            .eq(BaseDO::getTenantId, tenantId.getId())
            .eq(RoleMessageConfigDO::getRoleId, roleId.getId())
            .isNull(BaseDO::getDeletedAt);
    return this.baseMapper.selectList(queryWrapper);
  }

  public void removeByRoleId(@NotNull TenantId tenantId, @NotNull RoleId roleId) {
    // 更新为entity
    RoleMessageConfigDO entity = new RoleMessageConfigDO();
    entity.setDeletedAt(LocalDateTime.now());

    // 更新条件
    Wrapper<RoleMessageConfigDO> wrapper =
        Helper.getUpdateWrapper(RoleMessageConfigDO.class)
            .eq(BaseDO::getTenantId, tenantId.getId())
            .eq(RoleMessageConfigDO::getRoleId, roleId.getId());
    this.baseMapper.update(entity, wrapper);
  }

  public List<NoticeSendChannel> getRoleConfigByTenantId(
      TenantId tenantId, MessageTypeId messageTypeId) {
    if (tenantId == null || messageTypeId == null) {
      return new ArrayList<>();
    }
    return this.baseMapper.getSendChannelByTenantId(tenantId.getId(), messageTypeId.getId())
        .stream()
        .map(NoticeSendChannel::valueOf)
        .collect(Collectors.toList());
  }

  /** 获取指定消息需要发送的用户Ids */
  public List<UserId> getTargetUserIds(TenantId tenantId, MessageTypeId messageTypeId) {
    if (tenantId == null || messageTypeId == null) {
      return new ArrayList<>(0);
    }
    return this.baseMapper.getTargetUsrIds(tenantId.getId(), messageTypeId.getId()).stream()
        .map(UserId::new)
        .collect(Collectors.toList());
  }
}

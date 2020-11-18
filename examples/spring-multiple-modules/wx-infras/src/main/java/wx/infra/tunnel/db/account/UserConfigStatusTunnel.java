package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.common.data.account.UserConfigKey;
import wx.common.data.account.UserConfigStatus;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.UserConfigStatusMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Slf4j
@Component
public class UserConfigStatusTunnel
    extends ServiceImpl<UserConfigStatusMapper, UserConfigStatusDO> {

  /** 获取用户最新的配置记录 */
  public UserConfigStatusDO getLatest(UserId userId, UserConfigKey key) {

    Wrapper<UserConfigStatusDO> queryWrapper =
        Helper.getQueryWrapper(UserConfigStatusDO.class)
            .eq(UserConfigStatusDO::getUserId, userId.getId())
            .eq(UserConfigStatusDO::getKey, key)
            .orderByDesc(BaseDO::getCreatedAt)
            .last("LIMIT 1");
    return this.baseMapper.selectOne(queryWrapper);
  }

  /** 保存新的配置 */
  public void save(TenantId tenantId, UserId userId, UserConfigKey key, String value) {
    save(tenantId, userId, key, value, UserConfigStatus.UN_USED);
  }

  /** 保存配置信息 */
  public void save(
      TenantId tenantId, UserId userId, UserConfigKey key, String value, UserConfigStatus status) {

    UserConfigStatusDO entity = new UserConfigStatusDO();
    entity
        .setTenantId(tenantId.getId())
        .setStatus(status)
        .setKey(key)
        .setValue(value)
        .setUserId(userId.getId());
    this.save(entity);
  }

  /** 清除指定用户的之前未使用的值（更新为作废） */
  public void cleanUnused(TenantId tenantId, UserId userId, UserConfigKey key) {
    UserConfigStatusDO entity = new UserConfigStatusDO();
    entity.setDeletedAt(LocalDateTime.now()).setStatus(UserConfigStatus.OBSOLETE);

    Wrapper<UserConfigStatusDO> updateWrapper =
        Helper.getUpdateWrapper(UserConfigStatusDO.class)
            .eq(UserConfigStatusDO::getTenantId, tenantId.getId())
            .eq(UserConfigStatusDO::getUserId, userId.getId())
            .eq(UserConfigStatusDO::getKey, key)
            .eq(UserConfigStatusDO::getStatus, UserConfigStatus.UN_USED);
    this.update(entity, updateWrapper);
  }
}

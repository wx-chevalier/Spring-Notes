package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.PermissionMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
public class PermissionTunnel extends ServiceImpl<PermissionMapper, PermissionDO> {
  /**
   * 检查给定的权限列表是否均存在
   *
   * @return 返回不存在的权限ID列表,若为空，则表示全部存在
   */
  @SuppressWarnings("unchecked")
  public Set<Long> checkAllExist(Set<Long> permissionIds) {
    if (CollectionUtils.isEmpty(permissionIds)) {
      return Collections.EMPTY_SET;
    }
    Wrapper<PermissionDO> queryWrapper =
        Helper.permissionQ().in(PermissionDO::getId, permissionIds);
    List<PermissionDO> permissionDOS = this.baseMapper.selectList(queryWrapper);
    Set<Long> existIds =
        permissionDOS.stream().map(PermissionDO::getId).collect(Collectors.toSet());
    return Sets.difference(permissionIds, existIds);
  }

  /** 通过权限名称获取权限信息 */
  public List<PermissionDO> list(Set<String> permissionNameSet) {
    if (CollectionUtils.isEmpty(permissionNameSet)) {
      return new ArrayList<>(0);
    }

    Wrapper<PermissionDO> queryWrapper =
        Helper.getQueryWrapper(PermissionDO.class)
            .in(PermissionDO::getName, permissionNameSet)
            .isNull(BaseDO::getDeletedAt);
    return this.baseMapper.selectList(queryWrapper);
  }
}

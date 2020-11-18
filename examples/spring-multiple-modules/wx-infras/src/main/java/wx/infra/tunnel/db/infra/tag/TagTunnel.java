package wx.infra.tunnel.db.infra.tag;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.TagId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.tag.TagMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagTunnel extends ServiceImpl<TagMapper, TagDO> {

  /** 断言给定的标签存在并且类型为指定类型 */
  public Boolean existByIds(TenantId tenantId, Collection<TagId> tagIds, TagType type) {
    if (CollectionUtils.isEmpty(tagIds)) {
      return true;
    }
    Wrapper<TagDO> queryWrapper =
        Helper.getQueryWrapper(TagDO.class)
            .in(TagDO::getId, tagIds.stream().map(BaseEntityId::getId).collect(Collectors.toList()))
            .eq(BaseDO::getTenantId, tenantId.getId())
            .eq(TagDO::getTagType, type)
            .isNull(BaseDO::getDeletedAt);
    return this.baseMapper.selectCount(queryWrapper) == tagIds.size();
  }

  public List<TagDO> getAll(TenantId tenantId, Collection<TagId> tagIds, TagType type) {
    if (CollectionUtils.isEmpty(tagIds)) {
      return new ArrayList<>(0);
    }

    Wrapper<TagDO> queryWrapper =
        Helper.getQueryWrapper(TagDO.class)
            .in(TagDO::getId, tagIds.stream().map(BaseEntityId::getId).collect(Collectors.toSet()))
            .eq(BaseDO::getTenantId, tenantId.getId())
            .eq(TagDO::getTagType, type)
            .isNull(BaseDO::getDeletedAt);
    return this.baseMapper.selectList(queryWrapper);
  }
}

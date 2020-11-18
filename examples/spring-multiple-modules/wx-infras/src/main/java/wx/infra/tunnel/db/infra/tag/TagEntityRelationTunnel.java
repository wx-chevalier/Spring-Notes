package wx.infra.tunnel.db.infra.tag;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.common.data.shared.id.*;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.tag.TagEntityRelationMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagEntityRelationTunnel
    extends ServiceImpl<TagEntityRelationMapper, TagEntityRelationDO> {

  public boolean add(TenantId tenantId, UserId creatorId, EntityId entityId, TagId tagId) {
    if (count(
            new LambdaQueryWrapper<TagEntityRelationDO>()
                .eq(TagEntityRelationDO::getTenantId, tenantId.getId())
                .eq(TagEntityRelationDO::getCreatorId, creatorId.getId())
                .eq(TagEntityRelationDO::getEntityId, entityId.getId())
                .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType())
                .eq(TagEntityRelationDO::getTagId, tagId.getId()))
        == 0) {
      return save(
          new TagEntityRelationDO()
              .setTenantId(tenantId.getId())
              .setCreatorId(creatorId.getId())
              .setEntityId(entityId.getId())
              .setEntityType(entityId.getEntityType())
              .setTagId(tagId.getId()));
    } else {
      return update(
          new LambdaUpdateWrapper<TagEntityRelationDO>()
              .isNotNull(TagEntityRelationDO::getDeletedAt)
              .eq(TagEntityRelationDO::getTenantId, tenantId.getId())
              .eq(TagEntityRelationDO::getCreatorId, creatorId.getId())
              .eq(TagEntityRelationDO::getEntityId, entityId.getId())
              .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType())
              .eq(TagEntityRelationDO::getTagId, tagId.getId())
              .set(TagEntityRelationDO::getDeletedAt, null));
    }
  }

  public List<TagEntityRelationDO> list(TenantId tenantId, BaseEntityId entityId) {
    Wrapper<TagEntityRelationDO> queryWrapper =
        new LambdaQueryWrapper<TagEntityRelationDO>()
            .eq(TagEntityRelationDO::getTenantId, tenantId.getId())
            .eq(TagEntityRelationDO::getEntityId, entityId.getId())
            .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType())
            .isNull(BaseDO::getDeletedAt)
            .orderByDesc(TagEntityRelationDO::getId);

    return this.baseMapper.selectList(queryWrapper);
  }

  /**
   * 通过实体ID获取对应的标签ID
   *
   * @param entityId
   * @return 返回有效的标签ID {@link TagId}集合
   */
  public List<TagId> getTagIdsByEntityId(BaseEntityId entityId) {
    Wrapper<TagEntityRelationDO> queryWrapper =
        Helper.getQueryWrapper(TagEntityRelationDO.class)
            .eq(TagEntityRelationDO::getEntityId, entityId.getId())
            .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType())
            .isNull(BaseDO::getDeletedAt)
            .orderByDesc(TagEntityRelationDO::getCreatedAt);
    // 查询并处理数据
    List<TagEntityRelationDO> tagEntityRelationDOS = this.baseMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(tagEntityRelationDOS)) {
      return new ArrayList<>(0);
    }
    return tagEntityRelationDOS.stream()
        .map(TagEntityRelationDO::getTagId)
        .map(TagId::new)
        .collect(Collectors.toList());
  }

  public void cleanAndSave(BaseEntityId entityId, List<TagEntityRelationDO> tagEntityRelations) {

    // 逻辑删除实体资源
    Wrapper<TagEntityRelationDO> updateWrapper =
        Helper.getUpdateWrapper(TagEntityRelationDO.class)
            .eq(TagEntityRelationDO::getEntityId, entityId.getId())
            .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType())
            .isNull(BaseDO::getDeletedAt)
            .set(BaseDO::getDeletedAt, LocalDateTime.now());
    this.update(updateWrapper);

    // 新增资源
    this.saveBatch(tagEntityRelations);
  }
}

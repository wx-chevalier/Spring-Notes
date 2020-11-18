package wx.application.infra.tag;

import static java.util.stream.Collectors.toList;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.reactivex.rxjava3.core.Single;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Service;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.TagId;
import wx.common.data.shared.id.UserId;
import wx.domain.infra.tag.Tag;
import wx.domain.infra.tag.TagRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.infra.tag.TagDO;
import wx.infra.tunnel.db.infra.tag.TagEntityRelationDO;
import wx.infra.tunnel.db.infra.tag.TagEntityRelationTunnel;
import wx.infra.tunnel.db.infra.tag.TagTunnel;
import wx.infra.tunnel.db.mapper.infra.tag.TagMapper;

@Service
public class TagRepositoryImpl
    extends MyBatisIdBasedEntityRepository<TagTunnel, TagMapper, TagDO, Tag, TagId>
    implements TagRepository {

  @Getter(AccessLevel.PROTECTED)
  private TagConverter converter;

  private TagEntityRelationTunnel tagEntityRelationTunnel;

  public TagRepositoryImpl(
      TagTunnel tagTunnel,
      TagMapper mapper,
      TagConverter converter,
      TagEntityRelationTunnel tagEntityRelationTunnel) {
    super(tagTunnel, mapper);
    this.converter = converter;
    this.tagEntityRelationTunnel = tagEntityRelationTunnel;
  }

  @Override
  public Collection<Tag> findByTagType(TenantId tenantId, TagType tagType) {
    return getTunnel()
        .list(
            new LambdaQueryWrapper<TagDO>()
                .isNull(TagDO::getDeletedAt)
                .eq(TagDO::getTenantId, tenantId.getId())
                .eq(TagDO::getTagType, tagType))
        .stream()
        .map(getConverter()::convertFrom)
        .collect(toList());
  }

  @Override
  public Optional<Tag> findTag(TenantId tenantId, TagType tagType, String tag) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<TagDO>()
                        .isNull(TagDO::getDeletedAt)
                        .eq(TagDO::getTenantId, tenantId.getId())
                        .eq(TagDO::getTagType, tagType)
                        .eq(TagDO::getTag, tag)))
        .map(getConverter()::convertFrom);
  }

  @Override
  public boolean removeTag(TenantId tenantId, TagType tagType, String tag) {
    return getTunnel()
        .update(
            new LambdaUpdateWrapper<TagDO>()
                .isNull(TagDO::getDeletedAt)
                .eq(TagDO::getTenantId, tenantId.getId())
                .eq(TagDO::getTagType, tagType)
                .eq(TagDO::getTag, tag)
                .set(TagDO::getDeletedAt, LocalDateTime.now()));
  }

  @Override
  public Single<Collection<Tag>> findByEntityTags(TenantId tenantId, EntityId entityId) {
    return findByIds(
        tenantId,
        tagEntityRelationTunnel
            .list(
                new LambdaQueryWrapper<TagEntityRelationDO>()
                    .isNull(TagEntityRelationDO::getDeletedAt)
                    .eq(TagEntityRelationDO::getTenantId, tenantId.getId())
                    .eq(TagEntityRelationDO::getEntityId, entityId.getId())
                    .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType()))
            .stream()
            .map(TagEntityRelationDO::getTagId)
            .map(TagId::new)
            .collect(Collectors.toSet()));
  }

  @Override
  public Single<Collection<Tag>> findByEntityTags(
      TenantId tenantId, EntityId entityId, TagType tagType) {
    return findByEntityTags(tenantId, entityId)
        .map(tags -> tags.stream().filter(tag -> tag.getType() == tagType).collect(toList()));
  }

  @Override
  public Single<Collection<Tag>> findByEntityTags(
      TenantId tenantId, UserId creatorId, EntityId entityId) {
    return findByIds(
        tenantId,
        tagEntityRelationTunnel
            .list(
                new LambdaQueryWrapper<TagEntityRelationDO>()
                    .isNull(TagEntityRelationDO::getDeletedAt)
                    .eq(TagEntityRelationDO::getTenantId, tenantId.getId())
                    .eq(TagEntityRelationDO::getCreatorId, creatorId.getId())
                    .eq(TagEntityRelationDO::getEntityId, entityId.getId())
                    .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType()))
            .stream()
            .map(TagEntityRelationDO::getTagId)
            .map(TagId::new)
            .collect(toList()));
  }

  @Override
  public Single<Collection<Tag>> findByEntityTags(
      TenantId tenantId, UserId creatorId, EntityId entityId, TagType tagType) {
    return findByEntityTags(tenantId, creatorId, entityId)
        .map(tags -> tags.stream().filter(tag -> tag.getType() == tagType).collect(toList()));
  }

  @Override
  public boolean tagEntity(TenantId tenantId, UserId creatorId, EntityId entityId, TagId tagId) {
    return tagEntityRelationTunnel.add(tenantId, creatorId, entityId, tagId);
  }

  @Override
  public boolean removeEntityTag(TenantId tenantId, EntityId entityId, TagId tagId) {
    return tagEntityRelationTunnel.remove(
        new LambdaQueryWrapper<TagEntityRelationDO>()
            .isNull(TagEntityRelationDO::getDeletedAt)
            .eq(TagEntityRelationDO::getTenantId, tenantId.getId())
            .eq(TagEntityRelationDO::getEntityId, entityId.getId())
            .eq(TagEntityRelationDO::getEntityType, entityId.getEntityType())
            .eq(TagEntityRelationDO::getTagId, tagId.getId()));
  }

  @Override
  public Boolean assertExists(TenantId tenantId, List<TagId> tagIds, TagType tagType) {
    return getTunnel().existByIds(tenantId, tagIds, tagType);
  }
}

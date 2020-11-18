package wx.domain.infra.tag;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.TagId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntityRepository;

public interface TagRepository extends IdBasedEntityRepository<TagId, Tag> {

  Collection<Tag> findByTagType(TenantId tenantId, TagType tagType);

  Optional<Tag> findTag(TenantId tenantId, TagType tagType, String tag);

  boolean removeTag(TenantId tenantId, TagType tagType, String tag);

  Single<Collection<Tag>> findByEntityTags(TenantId tenantId, EntityId entityId);

  Single<Collection<Tag>> findByEntityTags(TenantId tenantId, EntityId entityId, TagType tagType);

  Single<Collection<Tag>> findByEntityTags(TenantId tenantId, UserId creatorId, EntityId entityId);

  Single<Collection<Tag>> findByEntityTags(
      TenantId tenantId, UserId creatorId, EntityId entityId, TagType tagType);

  boolean tagEntity(TenantId tenantId, UserId creatorId, EntityId entityId, TagId tagId);

  boolean removeEntityTag(TenantId tenantId, EntityId entityId, TagId tagId);

  Boolean assertExists(TenantId tenantId, List<TagId> tagIds, TagType tagType);
}

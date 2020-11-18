package wx.application.infra.tag;

import java.util.Collection;
import java.util.List;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.TagId;
import wx.domain.infra.tag.Tag;

public interface TagQueryService {
  /** 断言给定的标签集合全部存在，并且类型为指定类型 */
  Boolean assertExist(TenantId tenantId, Collection<TagId> tagIds, TagType tagType);

  List<Tag> findAll(TenantId tenantId, Collection<TagId> tagIds, TagType type);
}

package wx.application.infra.tag;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.TagId;
import wx.domain.infra.tag.Tag;
import wx.infra.tunnel.db.infra.tag.TagDO;
import wx.infra.tunnel.db.infra.tag.TagTunnel;

@Service
public class TagQueryServiceImpl implements TagQueryService {

  private TagTunnel tagTunnel;

  private TagConverter tagConverter;

  public TagQueryServiceImpl(TagTunnel tagTunnel, TagConverter tagConverter) {
    this.tagTunnel = tagTunnel;
    this.tagConverter = tagConverter;
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean assertExist(TenantId tenantId, Collection<TagId> tagIds, TagType tagType) {
    return tagTunnel.existByIds(tenantId, tagIds, tagType);
  }

  @Override
  public List<Tag> findAll(TenantId tenantId, Collection<TagId> tagIds, TagType type) {
    List<TagDO> tagListDO = tagTunnel.getAll(tenantId, tagIds, type);
    return tagListDO.stream().map(tagConverter::convertFrom).collect(Collectors.toList());
  }
}

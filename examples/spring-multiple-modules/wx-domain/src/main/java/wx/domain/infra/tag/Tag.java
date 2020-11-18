package wx.domain.infra.tag;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.infra.TagType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.TagId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends IdBasedEntity<TagId, Tag> {

  private UserId creatorId;

  private TagType type;

  private String tag;

  public Tag(
      TagId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String tag,
      UserId creatorId,
      TagType type) {
    super(id, tenantId, createdAt, updatedAt);
    this.tag = tag;
    this.creatorId = creatorId;
    this.type = type;
    this.tag = tag;
  }

  public Tag(TenantId tenantId, String tag, UserId creatorId, TagType type) {
    super(tenantId);
    this.tag = tag;
    this.creatorId = creatorId;
    this.type = type;
    this.tag = tag;
  }
}

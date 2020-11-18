package wx.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.shared.HasId;
import wx.common.data.shared.HasTimeFields;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
@SuppressWarnings("unchecked")
public abstract class IdBasedEntity<Id extends EntityId, E extends IdBasedEntity<Id, E>>
    implements HasTimeFields, HasId<Id> {

  private Id id;

  private TenantId tenantId;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  protected IdBasedEntity(Id id, TenantId tenantId) {
    this.id = id;
    this.tenantId = tenantId;
  }

  protected IdBasedEntity(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  public E setId(Id id) {
    this.id = id;
    return (E) this;
  }

  public E setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
    return (E) this;
  }

  public E setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return (E) this;
  }

  public E setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return (E) this;
  }
}

package wx.domain.event.message.admin.tenant;

import java.time.Instant;
import lombok.Data;
import lombok.Getter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.event.Event;

@Data
public class TenantEvent implements Event {

  private TenantEventType eventType;

  private TenantId tenantId;

  private String tenantName;

  private UserId tenantAdminId;

  @Getter Instant triggerAt = Instant.now();

  public TenantEvent(TenantEventType eventType) {
    this.eventType = eventType;
  }

  public static TenantEvent createEvent(
      TenantId tenantId, String tenantName, UserId tenantAdminId) {
    return new TenantEvent(TenantEventType.CREATED)
        .setTenantAdminId(tenantAdminId)
        .setTenantId(tenantId)
        .setTenantName(tenantName);
  }
}

package wx.infra.tunnel.db.shared;

import java.time.LocalDateTime;

public interface HasDeleteTimeField {
  LocalDateTime getDeletedAt();
}

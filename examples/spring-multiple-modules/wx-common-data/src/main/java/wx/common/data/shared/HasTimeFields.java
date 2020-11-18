package wx.common.data.shared;

import java.time.LocalDateTime;

public interface HasTimeFields {

  LocalDateTime getCreatedAt();

  LocalDateTime getUpdatedAt();
}

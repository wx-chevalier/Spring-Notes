package wx.common.data.infra.filestore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NonNull;

@Data
public class PreSignedUrl {

  private String url;

  private LocalDateTime expiresAt;

  @JsonCreator
  public PreSignedUrl(
      @JsonProperty("url") @NonNull String url,
      @JsonProperty("expiresAt") @NonNull LocalDateTime expiresAt) {
    this.url = url;
    this.expiresAt = expiresAt;
  }
}

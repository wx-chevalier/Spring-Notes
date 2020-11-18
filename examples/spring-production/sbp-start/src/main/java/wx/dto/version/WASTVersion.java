package wx.dto.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.ToString;

@ApiModel(description = "WAST 版本")
@ToString
public class WASTVersion {
  @Getter String version;
  @Getter String dirURL;
  @Getter String baseVersion;
  @Getter String baseDirURL;
  @Getter String type;
  @Getter String desc;

  @JsonCreator
  public WASTVersion(
      @JsonProperty("version") String version,
      @JsonProperty("dirUrl") String dirURL,
      @JsonProperty("baseVersion") String baseVersion,
      @JsonProperty("baseDirUrl") String baseDirURL,
      @JsonProperty("type") String type,
      @JsonProperty("desc") String desc) {
    this.version = version;
    this.dirURL = dirURL;
    this.baseVersion = baseVersion;
    this.baseDirURL = baseDirURL;
    this.type = type;
    this.desc = desc;
  }
}

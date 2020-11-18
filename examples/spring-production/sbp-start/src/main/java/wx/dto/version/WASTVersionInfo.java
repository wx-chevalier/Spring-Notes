package wx.dto.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ApiModel(description = "WAST 版本信息")
@ToString
public class WASTVersionInfo {
  @Getter List<WASTVersion> versions;

  @JsonCreator
  public WASTVersionInfo(@JsonProperty("versions") List<WASTVersion> versions) {
    this.versions = versions;
  }
}

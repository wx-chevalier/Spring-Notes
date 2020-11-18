package wx.api.rest.shared.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileResource extends NamedResource {
  private String fileUrl;

  private String md5;
}

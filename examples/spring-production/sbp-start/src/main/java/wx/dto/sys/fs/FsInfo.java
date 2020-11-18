package wx.dto.sys.fs;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel("文件系统信息")
@AllArgsConstructor
@Data
public class FsInfo {

  /** 文件系统名称 */
  String name;

  /** 全部空间 */
  long total;

  /** 可用空间 */
  long usable;
}

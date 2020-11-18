package wx.dto.sys.memory;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import wx.dto.sys.fs.FsInfo;

@ApiModel(description = "OsInfo, 操作系统描述信息")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OsInfo {

  /** 启动时间 */
  String uptimeDesc = "";

  /** 全部 CPU */
  long totalCpu;

  /** 空闲 CPU */
  long idleCpu;

  /** 可用内存 */
  long availableMemory = 0L;

  /** 总体内存 */
  long totalMemory = 0L;

  /** 内存占比描述 */
  String memoryDesc = "";

  /** 文件系统的信息 */
  List<FsInfo> fsInfoList;
}

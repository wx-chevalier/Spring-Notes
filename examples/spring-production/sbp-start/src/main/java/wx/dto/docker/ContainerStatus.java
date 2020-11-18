package wx.dto.docker;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("容器状态")
@Data
public class ContainerStatus {
  /** 内存使用情况 */
  long memoryUsage = 0L;

  /** CPU 使用情况 */
  long cpuUsage = 0L;
}

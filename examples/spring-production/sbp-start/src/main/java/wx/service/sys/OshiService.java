package wx.service.sys;

import java.util.concurrent.CompletableFuture;
import wx.dto.sys.memory.OsInfo;

/**
 * 系统与硬件信息获取的服务
 *
 * @author wxyyxc1992
 */
public interface OshiService {
  CompletableFuture<OsInfo> getOsInfo() throws InterruptedException;
}

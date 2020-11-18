package wx.domain.sysinfo.metrics;

import java.util.List;
import java.util.Optional;
import wx.dto.sys.network.NetworkInterfaceLoad;
import wx.dto.sys.network.NetworkInterfaceMetric;

public interface NetworkMetrics {
  List<NetworkInterfaceMetric> networkInterfaces();

  Optional<NetworkInterfaceMetric> networkInterfaceById(String id);

  List<NetworkInterfaceLoad> networkInterfaceLoads();

  Optional<NetworkInterfaceLoad> networkInterfaceLoadById(String id);
}

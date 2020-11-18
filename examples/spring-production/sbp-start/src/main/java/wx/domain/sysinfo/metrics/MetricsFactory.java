package wx.domain.sysinfo.metrics;

import wx.dto.sys.system.SystemLoad;

public interface MetricsFactory {
  CpuMetrics cpuMetrics();

  MemoryMetrics memoryMetrics();

  NetworkMetrics networkMetrics();

  default SystemLoad consolidatedMetrics() {
    return new SystemLoad(
        cpuMetrics().uptime(),
        cpuMetrics().cpuLoad(),
        networkMetrics().networkInterfaceLoads(),
        memoryMetrics().memoryLoad());
  }
}

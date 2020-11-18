package wx.domain.sysinfo.metrics;

import wx.dto.sys.cpu.CpuInfo;
import wx.dto.sys.cpu.CpuLoad;

public interface CpuMetrics {
  CpuInfo cpuInfo();

  CpuLoad cpuLoad();

  long uptime();
}

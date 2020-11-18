package wx.dto.sys.system;

import java.util.List;
import lombok.Getter;
import wx.dto.sys.cpu.CpuLoad;
import wx.dto.sys.memory.MemoryLoad;
import wx.dto.sys.network.NetworkInterfaceLoad;

@Getter
public class SystemLoad {
  private long uptime;
  private CpuLoad cpuLoad;
  private List<NetworkInterfaceLoad> networkInterfaceLoads;
  private MemoryLoad memory;

  public SystemLoad(
      long uptime,
      CpuLoad cpuLoad,
      List<NetworkInterfaceLoad> networkInterfaceLoads,
      MemoryLoad memory) {
    this.uptime = uptime;
    this.cpuLoad = cpuLoad;
    this.networkInterfaceLoads = networkInterfaceLoads;
    this.memory = memory;
  }
}

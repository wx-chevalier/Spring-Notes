package wx.domain.sysinfo.metrics.impl;

import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import wx.domain.sysinfo.metrics.MemoryMetrics;
import wx.dto.sys.memory.MemoryLoad;

public class DefaultMemoryMetrics implements MemoryMetrics {
  private final HardwareAbstractionLayer hal;
  private final OperatingSystem operatingSystem;

  public DefaultMemoryMetrics(HardwareAbstractionLayer hal, OperatingSystem operatingSystem) {
    this.hal = hal;
    this.operatingSystem = operatingSystem;
  }

  @Override
  public MemoryLoad memoryLoad() {
    return new MemoryLoad(
        operatingSystem.getProcessCount(),
        hal.getMemory().getSwapTotal(),
        hal.getMemory().getSwapUsed(),
        hal.getMemory().getTotal(),
        hal.getMemory().getAvailable());
  }
}

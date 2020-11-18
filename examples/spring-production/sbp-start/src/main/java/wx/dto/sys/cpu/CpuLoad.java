package wx.dto.sys.cpu;

import java.util.List;
import lombok.Getter;

@Getter
public class CpuLoad {
  private double cpuLoadCountingTicks;
  private double cpuLoadOsMxBean;
  private double systemLoadAverage;
  private List<CoreLoad> coreLoads;
  private CpuHealth cpuHealth;
  private int processCount;
  private int threadCount;

  public CpuLoad(
      double cpuLoadCountingTicks,
      double cpuLoadOsMxBean,
      double systemLoadAverage,
      List<CoreLoad> coreLoads,
      CpuHealth cpuHealth,
      int processCount,
      int threadCount) {
    this.cpuLoadCountingTicks = cpuLoadCountingTicks;
    this.cpuLoadOsMxBean = cpuLoadOsMxBean;
    this.systemLoadAverage = systemLoadAverage;
    this.coreLoads = coreLoads;
    this.cpuHealth = cpuHealth;
    this.processCount = processCount;
    this.threadCount = threadCount;
  }
}

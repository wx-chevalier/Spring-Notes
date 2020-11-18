package wx.dto.sys.memory;

import lombok.Getter;

@Getter
public class MemoryLoad {
  private int numberOfProcesses;
  private long swapTotal;
  private long swapUsed;
  private long total;
  private long available;

  public MemoryLoad(
      int numberOfProcesses, long swapTotal, long swapUsed, long total, long available) {
    this.numberOfProcesses = numberOfProcesses;
    this.swapTotal = swapTotal;
    this.swapUsed = swapUsed;
    this.total = total;
    this.available = available;
  }
}

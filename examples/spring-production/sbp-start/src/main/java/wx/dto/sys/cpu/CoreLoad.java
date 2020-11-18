package wx.dto.sys.cpu;

import lombok.Getter;

@Getter
public class CoreLoad {
  private double user;
  private double nice;
  private double sys;
  private double idle;
  private double ioWait;
  private double irq;
  private double softIrq;
  private double steal;

  public CoreLoad(
      double user,
      double nice,
      double sys,
      double idle,
      double ioWait,
      double irq,
      double softIrq,
      double steal) {
    this.user = user;
    this.nice = nice;
    this.sys = sys;
    this.idle = idle;
    this.ioWait = ioWait;
    this.irq = irq;
    this.softIrq = softIrq;
    this.steal = steal;
  }
}

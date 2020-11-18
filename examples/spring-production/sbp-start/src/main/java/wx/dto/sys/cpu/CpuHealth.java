package wx.dto.sys.cpu;

import java.util.List;
import lombok.Getter;

@Getter
public class CpuHealth {
  private List<Double> temperatures;
  private double voltage;
  private double fanRpm;
  private double fanPercent;

  public CpuHealth(List<Double> temperatures, double voltage, double fanRpm, double fanPercent) {
    this.temperatures = temperatures;
    this.voltage = voltage;
    this.fanRpm = fanRpm;
    this.fanPercent = fanPercent;
  }
}

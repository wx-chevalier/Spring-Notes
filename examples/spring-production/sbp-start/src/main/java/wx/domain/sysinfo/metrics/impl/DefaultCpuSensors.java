package wx.domain.sysinfo.metrics.impl;

import java.util.Arrays;
import java.util.List;
import oshi.hardware.HardwareAbstractionLayer;
import wx.dto.sys.cpu.CpuHealth;

public class DefaultCpuSensors {
  private final HardwareAbstractionLayer hal;

  public DefaultCpuSensors(HardwareAbstractionLayer hal) {
    this.hal = hal;
  }

  protected CpuHealth cpuHealth() {
    List<Double> temperature = cpuTemperatures();
    double fanRpm = cpuFanRpm();
    double fanPercent = cpuFanPercent();
    double cpuVoltage = cpuVoltage();
    return new CpuHealth(temperature, cpuVoltage, fanRpm, fanPercent);
  }

  protected double cpuVoltage() {
    return hal.getSensors().getCpuVoltage();
  }

  protected List<Double> cpuTemperatures() {
    return Arrays.asList(hal.getSensors().getCpuTemperature());
  }

  protected double cpuFanRpm() {
    return Arrays.stream(hal.getSensors().getFanSpeeds()).findFirst().orElse(0);
  }

  protected double cpuFanPercent() {
    return 0;
  }
}

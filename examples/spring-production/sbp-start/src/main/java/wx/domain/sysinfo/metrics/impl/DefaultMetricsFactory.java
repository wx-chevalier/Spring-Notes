package wx.domain.sysinfo.metrics.impl;

import javax.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import wx.domain.sysinfo.metrics.CpuMetrics;
import wx.domain.sysinfo.metrics.MemoryMetrics;
import wx.domain.sysinfo.metrics.MetricsFactory;
import wx.domain.sysinfo.metrics.NetworkMetrics;
import wx.utils.SpeedMeasurementManager;
import wx.utils.Ticker;

@Component
public class DefaultMetricsFactory implements MetricsFactory {
  private final HardwareAbstractionLayer hal;
  private final OperatingSystem operatingSystem;
  private final Ticker ticker;
  private final SpeedMeasurementManager speedMeasurementManager;

  @Setter private CpuMetrics cpuMetrics;
  @Setter private MemoryMetrics memoryMetrics;
  @Setter private NetworkMetrics networkMetrics;

  public DefaultMetricsFactory(
      HardwareAbstractionLayer hal,
      OperatingSystem operatingSystem,
      Ticker ticker,
      SpeedMeasurementManager speedMeasurementManager) {
    this.hal = hal;
    this.operatingSystem = operatingSystem;
    this.ticker = ticker;
    this.speedMeasurementManager = speedMeasurementManager;
  }

  @PostConstruct
  public void initialize() {
    DefaultCpuMetrics cpuMetrics =
        new DefaultCpuMetrics(hal, operatingSystem, ticker, new DefaultCpuSensors(hal));
    cpuMetrics.register();
    setCpuMetrics(cpuMetrics);

    setMemoryMetrics(new DefaultMemoryMetrics(hal, operatingSystem));

    DefaultNetworkMetrics networkMetrics = new DefaultNetworkMetrics(hal, speedMeasurementManager);
    networkMetrics.register();
    setNetworkMetrics(networkMetrics);
  }

  @Bean
  @Override
  public CpuMetrics cpuMetrics() {
    return cpuMetrics;
  }

  @Bean
  @Override
  public MemoryMetrics memoryMetrics() {
    return memoryMetrics;
  }

  @Bean
  @Override
  public NetworkMetrics networkMetrics() {
    return networkMetrics;
  }
}

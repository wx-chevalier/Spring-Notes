package wx.domain.sysinfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

@Configuration
public class SysInfoConfig {
  private SystemInfo systemInfo;

  public SysInfoConfig() {
    this.systemInfo = new SystemInfo();
  }

  @Bean
  public HardwareAbstractionLayer hal() {
    return systemInfo.getHardware();
  }

  @Bean
  public OperatingSystem os() {
    return systemInfo.getOperatingSystem();
  }

  @Bean
  public PlatformEnum platform() {
    return SystemInfo.getCurrentPlatformEnum();
  }
}

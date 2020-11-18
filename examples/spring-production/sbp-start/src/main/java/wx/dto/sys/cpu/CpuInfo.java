package wx.dto.sys.cpu;

import lombok.Getter;

@Getter
public class CpuInfo {
  private CentralProcessor centralProcessor;

  public CpuInfo(oshi.hardware.CentralProcessor centralProcessor) {
    this.centralProcessor =
        new CentralProcessor(
            centralProcessor.getLogicalProcessorCount(),
            centralProcessor.getPhysicalProcessorCount(),
            centralProcessor.getSystemUptime(),
            centralProcessor.getName(),
            centralProcessor.getSystemLoadAverage(),
            centralProcessor.getIdentifier(),
            centralProcessor.getFamily(),
            centralProcessor.getVendor(),
            centralProcessor.getVendorFreq(),
            centralProcessor.getModel(),
            centralProcessor.getStepping(),
            centralProcessor.isCpu64bit());
  }
}

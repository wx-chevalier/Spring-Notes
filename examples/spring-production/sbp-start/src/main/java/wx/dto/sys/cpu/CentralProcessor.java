package wx.dto.sys.cpu;

import lombok.Getter;

@Getter
public class CentralProcessor {
  private int logicalProcessorCount;
  private int physicalProcessorCount;
  private long systemUptime;
  private String name;
  private double systemLoadAverage;
  private String identifier;
  private String family;
  private String vendor;
  private long vendorFreq;
  private String model;
  private String stepping;
  private boolean cpu64bit;

  public CentralProcessor(
      int logicalProcessorCount,
      int physicalProcessorCount,
      long systemUptime,
      String name,
      double systemLoadAverage,
      String identifier,
      String family,
      String vendor,
      long vendorFreq,
      String model,
      String stepping,
      boolean cpu64bit) {
    this.logicalProcessorCount = logicalProcessorCount;
    this.physicalProcessorCount = physicalProcessorCount;
    this.systemUptime = systemUptime;
    this.name = name;
    this.systemLoadAverage = systemLoadAverage;
    this.identifier = identifier;
    this.family = family;
    this.vendor = vendor;
    this.vendorFreq = vendorFreq;
    this.model = model;
    this.stepping = stepping;
    this.cpu64bit = cpu64bit;
  }
}

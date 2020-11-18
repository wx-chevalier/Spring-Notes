package wx.service.sys.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;
import wx.dto.sys.fs.FsInfo;
import wx.dto.sys.memory.OsInfo;
import wx.service.sys.OshiService;

@Service
public class OshiServiceImpl implements OshiService {

  @Override
  @Async
  public CompletableFuture<OsInfo> getOsInfo() throws InterruptedException {
    SystemInfo si = new SystemInfo();

    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    OsInfo osInfo = new OsInfo();

    GlobalMemory globalMemory = hal.getMemory();
    osInfo.setTotalMemory(globalMemory.getTotal());
    osInfo.setAvailableMemory(globalMemory.getAvailable());
    osInfo.setMemoryDesc(
        String.format(
            "%s / %s",
            FormatUtil.formatBytes(globalMemory.getTotal() - globalMemory.getAvailable()),
            FormatUtil.formatBytes(globalMemory.getTotal())));

    CentralProcessor processor = hal.getProcessor();
    long[] prevTicks = processor.getSystemCpuLoadTicks();
    Util.sleep(1000);
    long[] ticks = processor.getSystemCpuLoadTicks();

    long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
    long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
    long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
    long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
    long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
    long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
    long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
    long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
    long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

    osInfo.setUptimeDesc(FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
    osInfo.setIdleCpu(idle);
    osInfo.setTotalCpu(totalCpu);

    FileSystem fileSystem = os.getFileSystem();
    OSFileStore[] fsArray = fileSystem.getFileStores();
    List<FsInfo> fsInfoList = new ArrayList<>();

    for (OSFileStore fs : fsArray) {
      long usable = fs.getUsableSpace();
      long total = fs.getTotalSpace();
      fsInfoList.add(new FsInfo(fs.getName(), total, usable));
    }

    osInfo.setFsInfoList(fsInfoList);

    return CompletableFuture.completedFuture(osInfo);
  }
}

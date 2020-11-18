package wx.controller.sys;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import wx.controller.exceptions.EntityNotFoundException;
import wx.domain.sysinfo.metrics.MetricsFactory;
import wx.dto.sys.cpu.CpuInfo;
import wx.dto.sys.cpu.CpuLoad;
import wx.dto.sys.memory.MemoryLoad;
import wx.dto.sys.network.NetworkInterfaceLoad;
import wx.dto.sys.network.NetworkInterfaceMetric;
import wx.dto.sys.system.SystemLoad;

@RestController
@RequestMapping("/system-info")
@PreAuthorize("hasRole('ADMIN')")
public class SystemInfoController {
  private MetricsFactory metricsFactory;

  public SystemInfoController(MetricsFactory metricsFactory) {
    this.metricsFactory = metricsFactory;
  }

  @GetMapping("/uptime")
  public long uptime() {
    return metricsFactory.cpuMetrics().uptime();
  }

  @ApiOperation(value = "系统负载汇总")
  @GetMapping("/system-load")
  public SystemLoad systemLoad() {
    return metricsFactory.consolidatedMetrics();
  }

  @GetMapping("/cpu-info")
  public CpuInfo cpuInfo() {
    return metricsFactory.cpuMetrics().cpuInfo();
  }

  @GetMapping("/cpu-load")
  public CpuLoad cpuLoad() {
    return metricsFactory.cpuMetrics().cpuLoad();
  }

  @GetMapping("/memory-load")
  public MemoryLoad memoryLoad() {
    return metricsFactory.memoryMetrics().memoryLoad();
  }

  @GetMapping("/network-interface")
  public List<NetworkInterfaceMetric> networkInterfaceList() {
    return metricsFactory.networkMetrics().networkInterfaces();
  }

  @GetMapping("/network-interface/{name}")
  public NetworkInterfaceMetric networkInterfaceById(@PathVariable("name") String name) {
    return metricsFactory
        .networkMetrics()
        .networkInterfaceById(name)
        .orElseThrow(() -> new EntityNotFoundException(name));
  }

  @GetMapping("/network-load")
  public List<NetworkInterfaceLoad> networkInterfaceLoadList() {
    return metricsFactory.networkMetrics().networkInterfaceLoads();
  }

  @GetMapping("/network-load/{name}")
  public NetworkInterfaceLoad networkInterfaceLoad(@PathVariable("name") String id) {
    return metricsFactory
        .networkMetrics()
        .networkInterfaceLoadById(id)
        .orElseThrow(() -> new EntityNotFoundException(id));
  }
}

package wx.domain.sysinfo.metrics.impl;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import wx.domain.sysinfo.metrics.NetworkMetrics;
import wx.dto.sys.network.NetworkInterfaceLoad;
import wx.dto.sys.network.NetworkInterfaceMetric;
import wx.dto.sys.network.NetworkInterfaceSpeed;
import wx.dto.sys.network.NetworkInterfaceValues;
import wx.utils.SpeedMeasurementManager;

@Slf4j
public class DefaultNetworkMetrics implements NetworkMetrics {
  protected static final NetworkInterfaceSpeed EMPTY_INTERFACE_SPEED =
      new NetworkInterfaceSpeed(0, 0);
  private static final int BYTE_TO_BIT = 8;
  private final HardwareAbstractionLayer hal;
  private final SpeedMeasurementManager speedMeasurementManager;

  public DefaultNetworkMetrics(
      HardwareAbstractionLayer hal, SpeedMeasurementManager speedMeasurementManager) {
    this.hal = hal;
    this.speedMeasurementManager = speedMeasurementManager;
  }

  void register() {
    speedMeasurementManager.register(
        Arrays.stream(hal.getNetworkIFs())
            .map(
                n ->
                    new SpeedMeasurementManager.SpeedSource() {
                      @Override
                      public String getName() {
                        return n.getName();
                      }

                      @Override
                      public long getCurrentRead() {
                        n.updateNetworkStats();
                        return n.getBytesRecv();
                      }

                      @Override
                      public long getCurrentWrite() {
                        n.updateNetworkStats();
                        return n.getBytesSent();
                      }
                    })
            .collect(Collectors.toList()));
  }

  @Override
  public List<NetworkInterfaceMetric> networkInterfaces() {
    return Arrays.stream(hal.getNetworkIFs())
        .map(mapToNetworkInterface())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<NetworkInterfaceMetric> networkInterfaceById(String id) {
    return Arrays.stream(hal.getNetworkIFs())
        .filter(n -> n.getName().equalsIgnoreCase(id))
        .map(mapToNetworkInterface())
        .findAny();
  }

  @Override
  public List<NetworkInterfaceLoad> networkInterfaceLoads() {
    return Arrays.stream(hal.getNetworkIFs()).map(mapToLoad()).collect(Collectors.toList());
  }

  @Override
  public Optional<NetworkInterfaceLoad> networkInterfaceLoadById(String id) {
    return Arrays.stream(hal.getNetworkIFs())
        .filter(n -> n.getName().equalsIgnoreCase(id))
        .map(mapToLoad())
        .findAny();
  }

  private Function<NetworkIF, NetworkInterfaceMetric> mapToNetworkInterface() {
    return nic -> {
      boolean loopback = false;
      try {
        loopback = nic.getNetworkInterface().isLoopback();
      } catch (SocketException e) {
        log.warn("Socket exception while queering for loopback parameter", e);
      }
      return new NetworkInterfaceMetric(
          nic.getName(),
          nic.getDisplayName(),
          nic.getMacaddr(),
          nic.getSpeed(),
          Stream.of(nic.getIPv4addr()).collect(Collectors.toList()),
          Stream.of(nic.getIPv6addr()).collect(Collectors.toList()),
          nic.getMTU(),
          loopback);
    };
  }

  private Function<NetworkIF, NetworkInterfaceLoad> mapToLoad() {
    return n -> {
      boolean up = false;
      try {
        up = n.getNetworkInterface().isUp();
      } catch (SocketException e) {
        log.error("Error occurred while getting status for NIC", e);
      }
      return new NetworkInterfaceLoad(
          n.getName(),
          up,
          new NetworkInterfaceValues(
              n.getSpeed(),
              n.getBytesRecv(),
              n.getBytesSent(),
              n.getPacketsRecv(),
              n.getPacketsSent(),
              n.getInErrors(),
              n.getOutErrors()),
          speedForInterfaceWithName(n.getName()));
    };
  }

  private NetworkInterfaceSpeed speedForInterfaceWithName(String name) {
    Optional<SpeedMeasurementManager.CurrentSpeed> currentSpeedForName =
        speedMeasurementManager.getCurrentSpeedForName(name);
    return currentSpeedForName
        .map(s -> new NetworkInterfaceSpeed(s.getReadPerSeconds(), s.getWritePerSeconds()))
        .orElse(EMPTY_INTERFACE_SPEED);
  }
}

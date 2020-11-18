package wx.dto.sys.network;

import java.util.List;
import lombok.Getter;

@Getter
public class NetworkInterfaceMetric {
  private String name;
  private String displayName;
  private String mac;
  private long speed;
  private List<String> ipv4;
  private List<String> ipv6;
  private int mtu;
  private boolean loopback;

  public NetworkInterfaceMetric(
      String name,
      String displayName,
      String mac,
      long speed,
      List<String> ipv4,
      List<String> ipv6,
      int mtu,
      boolean loopback) {
    this.name = name;
    this.displayName = displayName;
    this.mac = mac;
    this.speed = speed;
    this.ipv4 = ipv4;
    this.ipv6 = ipv6;
    this.mtu = mtu;
    this.loopback = loopback;
  }
}

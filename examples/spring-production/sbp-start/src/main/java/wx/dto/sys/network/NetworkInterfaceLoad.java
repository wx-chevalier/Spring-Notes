package wx.dto.sys.network;

import lombok.Getter;

@Getter
public class NetworkInterfaceLoad {
  private String name;
  private boolean up;
  private NetworkInterfaceValues values;
  private NetworkInterfaceSpeed speed;

  public NetworkInterfaceLoad(
      String name, boolean up, NetworkInterfaceValues values, NetworkInterfaceSpeed speed) {
    this.name = name;
    this.up = up;
    this.values = values;
    this.speed = speed;
  }
}

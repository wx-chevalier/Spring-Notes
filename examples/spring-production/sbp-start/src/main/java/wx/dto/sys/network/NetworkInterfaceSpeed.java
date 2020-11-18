package wx.dto.sys.network;

import lombok.Getter;

@Getter
public class NetworkInterfaceSpeed {
  private long receiveBytesPerSecond;
  private long sendBytesPerSecond;

  public NetworkInterfaceSpeed(long receiveBytesPerSecond, long sendBytesPerSecond) {
    this.receiveBytesPerSecond = receiveBytesPerSecond;
    this.sendBytesPerSecond = sendBytesPerSecond;
  }
}

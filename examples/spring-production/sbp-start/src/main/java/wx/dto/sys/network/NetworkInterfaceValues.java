package wx.dto.sys.network;

import lombok.Getter;

@Getter
public class NetworkInterfaceValues {
  private long speed;
  private long bytesReceived;
  private long bytesSent;
  private long packetsReceived;
  private long packetsSent;
  private long inErrors;
  private long outErrors;

  public NetworkInterfaceValues(
      long speed,
      long bytesReceived,
      long bytesSent,
      long packetsReceived,
      long packetsSent,
      long inErrors,
      long outErrors) {
    this.speed = speed;
    this.bytesReceived = bytesReceived;
    this.bytesSent = bytesSent;
    this.packetsReceived = packetsReceived;
    this.packetsSent = packetsSent;
    this.inErrors = inErrors;
    this.outErrors = outErrors;
  }
}

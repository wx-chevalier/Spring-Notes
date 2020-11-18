package wx.dto.sys.network;

import java.net.InetAddress;
import lombok.Data;

@Data
public class WsatInterfaceAddress {
  private InetAddress address = null;
  private InetAddress broadcast = null;
  private short maskLength = 0;
}

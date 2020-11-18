package wx.dto.sys.network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WsatNetworkInterface {
  private String name;
  private String displayName;
  private int index;
  private List<InetAddress> addrs;
  private List<WsatInterfaceAddress> interfaceAddressList;
  private List<InterfaceAddress> bindings[];
}

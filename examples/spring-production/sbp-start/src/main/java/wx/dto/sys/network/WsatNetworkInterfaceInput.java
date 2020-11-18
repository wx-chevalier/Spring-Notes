package wx.dto.sys.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** 更新接口的输入 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WsatNetworkInterfaceInput {
  String name;
  String address;
  String netmask;
  String broadcast;
  String gateway;

  Boolean isAdmin;
}

package wx.domain.terminal.config;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wx.dto.sys.network.WsatNetworkInterfaceInput;

/** 终端管理口配置 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminInterfaceConfig {
  User user;
  Map<String, WsatNetworkInterfaceInput> networks;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class User {
    String username;
    String password;
  }
}

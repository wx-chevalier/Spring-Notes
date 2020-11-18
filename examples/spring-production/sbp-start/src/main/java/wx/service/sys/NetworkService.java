package wx.service.sys;

import java.util.List;
import wx.dto.sys.network.WsatNetworkInterface;
import wx.dto.sys.network.WsatNetworkInterfaceInput;

/** 网络相关的服务 */
public interface NetworkService {

  public List<WsatNetworkInterface> getInterfaces();

  /** 从配置文件中获取到 */
  public List<WsatNetworkInterfaceInput> getInterfacesFromConfig();

  /** 使用命令行更新 */
  public Boolean updateInterface(WsatNetworkInterfaceInput interfaceInput);

  public Boolean updateInterfaceConfig(WsatNetworkInterfaceInput interfaceInput);
}

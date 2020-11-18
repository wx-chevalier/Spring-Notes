package wx.service.sys.impl;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import wx.domain.terminal.config.TerminalConfig;
import wx.dto.sys.network.WsatInterfaceAddress;
import wx.dto.sys.network.WsatNetworkInterface;
import wx.dto.sys.network.WsatNetworkInterfaceInput;
import wx.service.sys.NetworkService;

@Service
@Slf4j
public class NetworkServiceImpl implements NetworkService {

  @Override
  public List<WsatNetworkInterface> getInterfaces() {

    List<WsatNetworkInterface> interfaces = new ArrayList<>();

    try {
      Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

      // 遍历所有的网络接口
      while (e.hasMoreElements()) {
        NetworkInterface ni = e.nextElement();

        WsatNetworkInterface networkInterface = new WsatNetworkInterface();

        Enumeration<InetAddress> niE = ni.getInetAddresses();
        List<InetAddress> inetAddressArrayList = new ArrayList<>();

        // 遍历所有的地址
        while (niE.hasMoreElements()) {
          InetAddress i = niE.nextElement();
          inetAddressArrayList.add(i);
        }

        networkInterface.setName(ni.getName());
        networkInterface.setDisplayName(ni.getDisplayName());
        networkInterface.setDisplayName(ni.getDisplayName());
        networkInterface.setInterfaceAddressList(
            ni.getInterfaceAddresses().stream()
                .map(
                    interfaceAddress -> {
                      WsatInterfaceAddress wsatInterfaceAddress = new WsatInterfaceAddress();
                      wsatInterfaceAddress.setAddress(interfaceAddress.getAddress());
                      wsatInterfaceAddress.setBroadcast(interfaceAddress.getBroadcast());
                      wsatInterfaceAddress.setMaskLength(interfaceAddress.getNetworkPrefixLength());

                      return wsatInterfaceAddress;
                    })
                .collect(Collectors.toList()));
        networkInterface.setAddrs(inetAddressArrayList);

        interfaces.add(networkInterface);
      }

      return interfaces;

    } catch (Exception e) {
      e.printStackTrace();
      return interfaces;
    }
  }

  @Override
  public List<WsatNetworkInterfaceInput> getInterfacesFromConfig() {
    TerminalConfig terminalConfig = TerminalConfig.parseConfig();

    return new ArrayList<>(terminalConfig.getAdmin().getNetworks().values());
  }

  /**
   * 更新接口配置
   *
   * @param interfaceInput
   * @return
   */
  @Override
  public Boolean updateInterface(WsatNetworkInterfaceInput interfaceInput) {

    String command =
        "ifconfig "
            + interfaceInput.getName()
            + " "
            + interfaceInput.getAddress()
            + " netmask "
            + interfaceInput.getNetmask()
            + " broadcast "
            + interfaceInput.getBroadcast()
            + " && /etc/init.d/networking restart";

    try {

      Process p = Runtime.getRuntime().exec(command);

      InputStream is = p.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      p.waitFor();

      if (p.exitValue() != 0) {
        // 说明命令执行失败，可以进入到错误处理步骤中
        String s = null;
        while ((s = reader.readLine()) != null) {
          System.out.println(s);
        }

        return false;
      }
    } catch (IOException | InterruptedException e) {

      log.error(e.getMessage());

      return false;
    }

    return true;
  }

  @Override
  public Boolean updateInterfaceConfig(WsatNetworkInterfaceInput interfaceInput) {
    // 读取接口配置并且更新保存
    try {
      TerminalConfig terminalConfig = TerminalConfig.parseConfig();
      File file = new File("/etc/network/interfaces");

      // 获取当前所有的 IP 地址，构建新的地址
      List<WsatNetworkInterfaceInput> interfaces =
          new ArrayList<>(terminalConfig.getAdmin().getNetworks().values());
      StringBuilder newIfconfig = new StringBuilder();

      newIfconfig.append(
          "source /etc/network/interfaces.d/*\n"
              + "\n"
              + "# The loopback network interface\n"
              + "auto lo\n"
              + "iface lo inet loopback\n");

      interfaces.forEach(
          i -> {
            // 判断是否为传入的，如果为传入的则使用
            if (interfaceInput.getName().equals(i.getName())) {
              System.out.println(interfaceInput);
              i.setAddress(interfaceInput.getAddress());
              i.setBroadcast(interfaceInput.getBroadcast());
              i.setNetmask(interfaceInput.getNetmask());
              i.setGateway(interfaceInput.getGateway());

              newIfconfig.append(this.genIfConfigStr(interfaceInput));
            } else {
              newIfconfig.append(this.genIfConfigStr(i));
            }
          });

      newIfconfig.append(
          "# DNS\n" + "dns-nameservers 114.114.114.114\n" + "dns-nameservers 223.5.5.5");

      // 将文件写入到配置中
      FileUtils.writeStringToFile(file, newIfconfig.toString(), "utf-8");

      TerminalConfig.saveConfig(terminalConfig);

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private String genIfConfigStr(WsatNetworkInterfaceInput interfaceInput) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(
        String.format(
            "\n" + "auto %s\n" + "iface %s inet static\n" + "address %s\n" + "netmask %s\n",
            interfaceInput.getName(),
            interfaceInput.getName(),
            interfaceInput.getAddress(),
            interfaceInput.getNetmask()));

    if (!Strings.isEmpty(interfaceInput.getGateway())) {
      stringBuilder.append("gateway ").append(interfaceInput.getGateway()).append("\n");
    }

    return stringBuilder.toString();
  }
}

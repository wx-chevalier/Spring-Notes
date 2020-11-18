package wx.domain.terminal.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import java.io.File;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wx.utils.JSONUtils;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminalConfig {
  /** 管理员配置 */
  AdminInterfaceConfig admin;

  /** 终端设备信息: TODO 补全 */
  TerminalInfo terminal;

  /** Agent 配置 */
  Agent agent;

  /** 数据库配置 */
  DbConfig db;

  /** 队列配置 */
  //  RabbitMQConfig mq;

  /** 服务配置 */
  ServiceConfig service;

  /** Docker Registry */
  Registry registry;

  public static String configFile = "/etc/wsat/config.json";

  /** 解析 JSON 配置文件 */
  public static TerminalConfig parseConfig(File file) {
    return JSONUtils.readJSON(file, TerminalConfig.class);
  }

  public static TerminalConfig parseConfig() {
    return JSONUtils.readJSON(new File(configFile), TerminalConfig.class);
  }

  public static void saveConfig(TerminalConfig terminalConfig) {
    JSONUtils.saveJSON(terminalConfig, new File(configFile));
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Agent {
    String host;
    Integer port;
    String versions;
    String versionDir;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TerminalInfo {
    @SuppressWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    Info info;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Info {

      private String id;
      private String orgName;

      @SuppressWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
      private Date releaseDate;
    }
  }

  //  @Getter
  //  @NoArgsConstructor
  //  @AllArgsConstructor
  //  public static class RabbitMQConfig {
  //    String host;
  //
  //    ContainerConfig container =
  //        new ContainerConfig(
  //            "rabbitmq:3",
  //            "wsat-mq",
  //            Collections.singletonList(
  //                new VolumeConfig(
  //                    "wsat-mq", "/var/lib/rabbitmq", TerminalConfigDefaults.wsatRabbitBaseData)),
  //            asList(new PortMapping(4369), new PortMapping(5671), new PortMapping(5672)));
  //  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Registry {
    String host;
    String username;
    String password;
  }
}

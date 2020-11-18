package wx.domain.terminal.config;

import static java.util.Collections.singletonList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wx.domain.terminal.config.ContainerConfig.PortMapping;
import wx.domain.terminal.config.ContainerConfig.VolumeConfig;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DbConfig {
  MySQLConfig mysql;
  RedisConfig redis;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MySQLConfig {
    String host;
    Integer port;
    String user;
    String password;
    String database;

    @JsonIgnore
    ContainerConfig container =
        new ContainerConfig(
            "mysql:5.6",
            "wsat-mysql",
            singletonList(
                new VolumeConfig(
                    "wsat-mysql", "/var/lib/mysql", TerminalConfigDefaults.wsatMySqlBaseData)),
            singletonList(new PortMapping(3306, 3306)));
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RedisConfig {
    String host;
    Integer port;
    String password;

    @JsonIgnore
    ContainerConfig container =
        new ContainerConfig(
            "redis",
            "wsat-redis",
            singletonList(
                new VolumeConfig("wsat-redis", "/data", TerminalConfigDefaults.wsatRedisBaseData)),
            singletonList(new PortMapping(6379, 6379)));
  }
}

package wx.domain.terminal.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 设备中资源位置配置 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TerminalResourceConfig {
  String mySQLBase = "/mnt/wsat/base/data/volume.wsat-mysql.base.tar.gz";
  String redisBase = "/mnt/wsat/base/data/volume.wsat-redis.base.tar.gz";
  String pocBase = "/mnt/wsat/base/data/volume.wsat-poc.base.tar.gz";
}

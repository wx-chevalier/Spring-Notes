package wx.context.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperty {
  // 阿里云访问配置
  AliyunProperty aliyun = new AliyunProperty();

  // 应用安全相关配置
  SecurityProperty security = new SecurityProperty();

  // 用户上传文件目录
  String userUploadDir = "/mnt/wsat/uploads";

  // agent 工作目录，一些动态数据如锁将生成到该目录下
  String workdir = "/mnt/wsat/agent/";

  // 终端配置文件
  String terminalConfig = "/etc/wsat/config.json";

  // monitor 测量间隔 (ms)
  Long monitorMeasurementIntervalMS = 1000L;
}

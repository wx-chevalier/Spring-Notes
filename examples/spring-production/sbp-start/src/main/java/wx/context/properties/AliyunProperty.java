package wx.context.properties;

import lombok.Data;

@Data
public class AliyunProperty {

  // 阿里云访问 ID
  String accessKeyId;

  // 阿里云访问 Secret
  String accessKeySecret;

  // Endpoint以杭州为例，其它Region请按实际情况填写。
  String endpoint = "https://oss-cn-beijing.aliyuncs.com";
}

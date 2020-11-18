package wx.domain.poc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vuln {
  String name;
  VulnType type;
  VulnLevel level;
  String desc;
  String ref;

  @JsonProperty("vuln_id")
  String id;

  @JsonProperty("cnvd_id")
  String cnvdId;

  @JsonProperty("cve_id")
  String cveId;

  @JsonProperty("disclosure_date")
  String disclosureDate;

  String product;

  @JsonProperty("product_version")
  String productVersion;

  @JsonProperty("poc_ids")
  List<String> pocIds;

  // Python 类名
  @JsonProperty("__class__.__name__")
  String pyClassName;

  @JsonProperty("__file__.__md5__")
  String codeId;

  public enum VulnType {
    OTHER(0), // 其他
    INJECTION(1), // 注入
    XSS(2), // xss跨站脚本攻击
    XXE(3), // xml外部实体攻击
    FILE_UPLOAD(4), // 任意文件上传
    FILE_OPERATION(5), // 任意文件操作
    FILE_DOWNLOAD(6), // 任意文件下载
    FILE_TRAVERSAL(7), // 目录遍历
    RCE(8), // 远程命令/代码执行
    LFI(9), // 本地文件包含
    RFI(10), // 远程文件包含
    INFO_LEAK(11), // 信息泄漏
    MISCONFIGURATION(12); // 错误配置

    int code;

    @JsonValue
    public int getCode() {
      return code;
    }

    VulnType(int code) {
      this.code = code;
    }
  }

  public enum VulnLevel {
    LOW(1), // 低
    MED(2), // 中
    HIGH(3); // 高
    int code;

    @JsonValue
    public int getCode() {
      return code;
    }

    VulnLevel(int code) {
      this.code = code;
    }
  }
}

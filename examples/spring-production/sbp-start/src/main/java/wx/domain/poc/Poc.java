package wx.domain.poc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Poc {
  @JsonProperty("poc_id")
  String id;

  String name;

  String author;

  @JsonProperty("create_date")
  String createDate;

  @JsonProperty("option_schema")
  String optionSchema;

  @JsonProperty("vuln_id")
  String vulnId;

  // Python 类名
  @JsonProperty("__class__.__name__")
  String pyClassName;

  @JsonProperty("__file__.__md5__")
  String codeId;
}

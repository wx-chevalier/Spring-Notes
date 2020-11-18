package wx.domain.poc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {
  @JsonProperty("strategy_id")
  String id;

  String author;

  String name;

  String desc;

  @JsonProperty("poc_ids")
  List<String> pocIds;

  @JsonProperty("create_date")
  String createDate;

  // Python 类名
  @JsonProperty("__class__.__name__")
  String pyClassName;

  @JsonProperty("__file__.__md5__")
  String codeId;
}

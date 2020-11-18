package wx.common.data.infra.kv.query;

import java.util.List;
import lombok.Data;

@Data
public class BaseTsKvQuery implements TsKvQuery {
  private final List<String> keys;
  private final Long startTs;
  private final Long endTs;

  public BaseTsKvQuery(List<String> keys, Long startTs, Long endTs) {
    this.keys = keys;
    this.startTs = startTs;
    this.endTs = endTs;
  }
}

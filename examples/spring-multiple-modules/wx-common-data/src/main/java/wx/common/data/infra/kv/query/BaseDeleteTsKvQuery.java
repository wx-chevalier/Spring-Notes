package wx.common.data.infra.kv.query;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseDeleteTsKvQuery extends BaseTsKvQuery implements DeleteTsKvQuery {
  private final Boolean rewriteLatestIfDeleted;

  public BaseDeleteTsKvQuery(
      List<String> keys, Long startTs, Long endTs, Boolean rewriteLatestIfDeleted) {
    super(keys, startTs, endTs);
    this.rewriteLatestIfDeleted = rewriteLatestIfDeleted;
  }

  public BaseDeleteTsKvQuery(List<String> keys, long startTs, long endTs) {
    this(keys, startTs, endTs, false);
  }
}

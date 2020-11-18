package wx.common.data.infra.kv.query;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.infra.kv.Aggregation;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseReadTsKvQuery extends BaseTsKvQuery implements ReadTsKvQuery {
  private final Long interval;
  private final Integer limit;
  private final Aggregation aggregation;
  private final String orderBy;

  public BaseReadTsKvQuery(
      List<String> keys,
      Long startTs,
      Long endTs,
      Long interval,
      Integer limit,
      Aggregation aggregation) {
    super(keys, startTs, endTs);
    this.interval = interval;
    this.limit = limit;
    this.aggregation = aggregation;
    this.orderBy = "DESC";
  }

  public BaseReadTsKvQuery(
      List<String> keys,
      Long startTs,
      Long endTs,
      Long interval,
      Integer limit,
      Aggregation aggregation,
      String orderBy) {
    super(keys, startTs, endTs);
    this.interval = interval;
    this.limit = limit;
    this.aggregation = aggregation;
    this.orderBy = orderBy;
  }

  public BaseReadTsKvQuery(List<String> keys, long startTs, long endTs) {
    this(keys, startTs, endTs, endTs - startTs, 1, Aggregation.AVG, "DESC");
  }

  public BaseReadTsKvQuery(List<String> keys, long startTs, long endTs, int limit, String orderBy) {
    this(keys, startTs, endTs, endTs - startTs, limit, Aggregation.NONE, orderBy);
  }
}

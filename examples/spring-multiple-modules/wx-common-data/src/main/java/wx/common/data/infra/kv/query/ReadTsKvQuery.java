package wx.common.data.infra.kv.query;

import wx.common.data.infra.kv.Aggregation;

public interface ReadTsKvQuery extends TsKvQuery {
  Long getInterval();

  Integer getLimit();

  Aggregation getAggregation();

  String getOrderBy();
}

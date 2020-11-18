package wx.infra.service.kv;

import static com.google.common.base.Preconditions.checkArgument;

import org.springframework.util.CollectionUtils;
import wx.common.data.infra.kv.Aggregation;
import wx.common.data.infra.kv.query.DeleteTsKvQuery;
import wx.common.data.infra.kv.query.ReadTsKvQuery;

/**
 * TimeSeriesValidator.
 *
 * @author lotuc
 */
public class TimeSeriesValidator {
  public static void validateDeleteTsKvQuery(DeleteTsKvQuery query) {
    checkArgument(query != null, "DeleteTsKvQuery can't be null");
    checkArgument(
        !CollectionUtils.isEmpty(query.getKeys()), "Incorrect DeleteTsKvQuery. Key can't be empty");
  }

  public static void validateReadTsKvQuery(ReadTsKvQuery query, long maxTsIntervals) {
    checkArgument(query != null, "ReadTsKvQuery can't be null");
    checkArgument(
        !CollectionUtils.isEmpty(query.getKeys()), "Incorrect ReadTsKvQuery. Key can't be empty");
    checkArgument(
        query.getAggregation() != null, "Incorrect ReadTsKvQuery. Aggregation can't be empty");
    if (!Aggregation.NONE.equals(query.getAggregation())) {
      long step = Math.max(query.getInterval(), 1000);
      long intervalCounts = (query.getEndTs() - query.getStartTs()) / step;
      checkArgument(
          intervalCounts <= maxTsIntervals && intervalCounts > 0,
          "Incorrect TsKvQuery. Number of intervals is to high - "
              + intervalCounts
              + ". Please increase 'interval' parameter for your query or reduce the time range of the query.");
    }
  }
}

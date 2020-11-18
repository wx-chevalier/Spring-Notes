package wx.common.data.infra.kv.query;

import java.util.List;

public interface TsKvQuery {
  List<String> getKeys();

  Long getStartTs();

  Long getEndTs();
}

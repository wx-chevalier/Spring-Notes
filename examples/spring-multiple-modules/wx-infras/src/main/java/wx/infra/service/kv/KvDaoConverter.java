package wx.infra.service.kv;

import java.util.Optional;
import wx.common.data.infra.kv.BaseTsKvEntry;
import wx.common.data.infra.kv.BooleanEntry;
import wx.common.data.infra.kv.DoubleEntry;
import wx.common.data.infra.kv.KvEntry;
import wx.common.data.infra.kv.LongEntry;
import wx.common.data.infra.kv.StringEntry;
import wx.common.data.infra.kv.TsKvEntry;
import wx.infra.tunnel.db.infra.kv.TsKvDO;
import wx.infra.tunnel.db.infra.kv.TsKvLatestDO;

public class KvDaoConverter {

  @SuppressWarnings({"unchecked", "DuplicatedCode"})
  public static Optional<TsKvEntry> toTsKvEntry(TsKvDO tsKvDO) {
    KvEntry kvEntry = null;
    if (tsKvDO.getBoolV() != null) {
      kvEntry = new BooleanEntry(tsKvDO.getKey(), tsKvDO.getBoolV());
    } else if (tsKvDO.getLongV() != null) {
      kvEntry = new LongEntry(tsKvDO.getKey(), tsKvDO.getLongV());
    } else if (tsKvDO.getDblV() != null) {
      kvEntry = new DoubleEntry(tsKvDO.getKey(), tsKvDO.getDblV());
    } else if (tsKvDO.getStrV() != null) {
      kvEntry = new StringEntry(tsKvDO.getKey(), tsKvDO.getStrV());
    }
    return kvEntry == null
        ? Optional.empty()
        : Optional.of(new BaseTsKvEntry(tsKvDO.getTs(), kvEntry));
  }

  @SuppressWarnings({"unchecked", "DuplicatedCode"})
  public static Optional<TsKvEntry> toTsKvEntry(TsKvLatestDO tsKvLatestDO) {
    KvEntry kvEntry = null;
    if (tsKvLatestDO.getBoolV() != null) {
      kvEntry = new BooleanEntry(tsKvLatestDO.getKey(), tsKvLatestDO.getBoolV());
    } else if (tsKvLatestDO.getLongV() != null) {
      kvEntry = new LongEntry(tsKvLatestDO.getKey(), tsKvLatestDO.getLongV());
    } else if (tsKvLatestDO.getDblV() != null) {
      kvEntry = new DoubleEntry(tsKvLatestDO.getKey(), tsKvLatestDO.getDblV());
    } else if (tsKvLatestDO.getStrV() != null) {
      kvEntry = new StringEntry(tsKvLatestDO.getKey(), tsKvLatestDO.getStrV());
    }
    return kvEntry == null
        ? Optional.empty()
        : Optional.of(new BaseTsKvEntry(tsKvLatestDO.getTs(), kvEntry));
  }
}

package wx.infra.service.kv;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import wx.common.data.infra.kv.TsKvEntry;
import wx.common.data.infra.kv.query.DeleteTsKvQuery;
import wx.common.data.infra.kv.query.ReadTsKvQuery;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;

public interface TimeSeriesService {

  TimeSeriesSetting getTimeSeriesSetting();

  Flowable<TsKvEntry> findAll(TenantId tenantId, EntityId entityId, ReadTsKvQuery queries);

  Flowable<TsKvEntry> findLatest(TenantId tenantId, EntityId entityId, Collection<String> keys);

  Single<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId);

  Completable save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry);

  default Completable save(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry) {
    return save(tenantId, entityId, tsKvEntry, 0);
  }

  Completable save(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry, long ttl);

  Completable remove(TenantId tenantId, EntityId entityId, List<DeleteTsKvQuery> queries);
}

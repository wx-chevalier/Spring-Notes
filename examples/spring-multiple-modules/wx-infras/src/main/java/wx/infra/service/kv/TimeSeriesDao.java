package wx.infra.service.kv;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import wx.common.data.infra.kv.TsKvEntry;
import wx.common.data.infra.kv.query.DeleteTsKvQuery;
import wx.common.data.infra.kv.query.ReadTsKvQuery;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;

public interface TimeSeriesDao {
  Flowable<TsKvEntry> findAllAsync(
      TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries);

  Flowable<TsKvEntry> findAllByQueryAsync(
      TenantId tenantId, EntityId entityId, ReadTsKvQuery queries);

  Single<Optional<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, String key);

  Single<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId);

  default Completable save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
    return save(tenantId, entityId, tsKvEntry, 0);
  }

  Completable save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry, long ttl);

  default Completable savePartition(
      TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key) {
    return savePartition(tenantId, entityId, tsKvEntryTs, key, 0);
  }

  Completable savePartition(
      TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key, long ttl);

  Completable saveLatest(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry);

  Completable remove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query);

  Completable removeLatest(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query, String key);

  Completable removePartition(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query);
}

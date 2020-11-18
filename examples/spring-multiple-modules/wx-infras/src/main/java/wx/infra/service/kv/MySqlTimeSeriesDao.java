package wx.infra.service.kv;

import static java.util.stream.Collectors.toList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import wx.common.data.infra.kv.Aggregation;
import wx.common.data.infra.kv.KvConstants;
import wx.common.data.infra.kv.KvEntry;
import wx.common.data.infra.kv.TsKvEntry;
import wx.common.data.infra.kv.query.BaseReadTsKvQuery;
import wx.common.data.infra.kv.query.DeleteTsKvQuery;
import wx.common.data.infra.kv.query.ReadTsKvQuery;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.infra.tunnel.db.infra.kv.TsKvDO;
import wx.infra.tunnel.db.infra.kv.TsKvLatestDO;
import wx.infra.tunnel.db.infra.kv.TsKvLatestTunnel;
import wx.infra.tunnel.db.infra.kv.TsKvTunnel;

@Component
@Slf4j
public class MySqlTimeSeriesDao implements TimeSeriesDao {
  private TsKvTunnel tsKvTunnel;
  private TsKvLatestTunnel tsKvLatestTunnel;

  public MySqlTimeSeriesDao(TsKvTunnel tsKvTunnel, TsKvLatestTunnel tsKvLatestTunnel) {
    this.tsKvTunnel = tsKvTunnel;
    this.tsKvLatestTunnel = tsKvLatestTunnel;
  }

  @Override
  public Flowable<TsKvEntry> findAllAsync(
      TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries) {
    return Single.concat(
            queries.stream()
                .map(query -> findAllAsync(tenantId, entityId, query))
                .collect(toList()))
        .flatMap(Flowable::fromIterable);
  }

  @Override
  public Flowable<TsKvEntry> findAllByQueryAsync(
      TenantId tenantId, EntityId entityId, ReadTsKvQuery queries) {
    return Single.concat(Collections.singletonList(findAllAsync(tenantId, entityId, queries)))
        .flatMap(Flowable::fromIterable);
  }

  @Override
  public Single<Optional<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, String key) {
    return Single.create(
        emitter -> {
          TsKvLatestDO tsKvLatestDO =
              tsKvLatestTunnel.getOneByCompositeKey(
                  entityId.getId(), entityId.getEntityType(), key);
          if (tsKvLatestDO != null) {
            emitter.onSuccess(KvDaoConverter.toTsKvEntry(tsKvLatestDO));
          } else {
            emitter.onSuccess(Optional.empty());
          }
        });
  }

  @Override
  public Single<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId) {
    return Single.create(
        emitter -> {
          List<TsKvLatestDO> tsKvLatestDOs =
              tsKvLatestTunnel.listByEntityIdAndEntityType(
                  entityId.getId(), entityId.getEntityType());
          emitter.onSuccess(
              tsKvLatestDOs.stream()
                  .map(KvDaoConverter::toTsKvEntry)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .collect(toList()));
        });
  }

  @Override
  public Completable save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry, long ttl) {
    return Completable.create(
        emitter -> {
          try {
            TsKvDO entity =
                new TsKvDO()
                    .setEntityType(entityId.getEntityType().name())
                    .setEntityId(entityId.getId())
                    .setTs(tsKvEntry.getTs())
                    .setKey(tsKvEntry.getKey())
                    .setStrV(KvEntry.ofString(tsKvEntry).orElse(null))
                    .setDblV(KvEntry.ofDouble(tsKvEntry).orElse(null))
                    .setLongV(KvEntry.ofLong(tsKvEntry).orElse(null))
                    .setBoolV(KvEntry.ofBoolean(tsKvEntry).orElse(null));
            if (!tsKvTunnel.exists(entityId, tsKvEntry.getKey(), tsKvEntry.getTs())) {
              tsKvTunnel.save(entity);
            }
            emitter.onComplete();
          } catch (Throwable t) {
            emitter.onError(t);
          }
        });
  }

  @Override
  public Completable savePartition(
      TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key, long ttl) {
    // TODO:
    return Completable.complete();
  }

  @Override
  public Completable saveLatest(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
    return Completable.create(
        emitter -> {
          TsKvLatestDO latestEntity =
              new TsKvLatestDO()
                  .setEntityType(entityId.getEntityType().name())
                  .setEntityId(entityId.getId())
                  .setTs(tsKvEntry.getTs())
                  .setKey(tsKvEntry.getKey())
                  .setStrV(KvEntry.ofString(tsKvEntry).orElse(null))
                  .setDblV(KvEntry.ofDouble(tsKvEntry).orElse(null))
                  .setLongV(KvEntry.ofLong(tsKvEntry).orElse(null))
                  .setBoolV(KvEntry.ofBoolean(tsKvEntry).orElse(null));
          tsKvLatestTunnel.saveOrUpdate(latestEntity);
          emitter.onComplete();
        });
  }

  @Override
  public Completable remove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
    return Completable.create(
        emitter -> {
          tsKvTunnel.delete(
              entityId.getId(),
              entityId.getEntityType(),
              query.getKeys(),
              query.getStartTs(),
              query.getEndTs());
          emitter.onComplete();
        });
  }

  @Override
  public Completable removeLatest(
      TenantId tenantId, EntityId entityId, DeleteTsKvQuery query, String key) {
    return findLatest(tenantId, entityId, key)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(
            latestEntry -> {
              long ts = latestEntry.getTs();
              return ts > query.getStartTs() && ts <= query.getEndTs();
            })
        .map(
            shouldDeleteLatest -> {
              if (shouldDeleteLatest) {
                return tsKvLatestTunnel.removeByCompositeKey(
                    entityId.getId(), entityId.getEntityType(), key);
              } else {
                return false;
              }
            })
        .flatMapCompletable(
            isLatestRemoved -> {
              if (isLatestRemoved) {
                // reset latest
                return getNewLatestEntryFuture(tenantId, entityId, query);
              }
              return Completable.complete();
            });
  }

  @Override
  public Completable removePartition(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
    // TODO:
    return Completable.complete();
  }

  private Completable getNewLatestEntryFuture(
      TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
    long startTs = 0;
    long endTs = query.getStartTs() - 1;
    BaseReadTsKvQuery findNewLatestQuery =
        new BaseReadTsKvQuery(
            query.getKeys(),
            startTs,
            endTs,
            endTs - startTs,
            1,
            Aggregation.NONE,
            KvConstants.ORDER_DESC);
    return findAllAsync(tenantId, entityId, findNewLatestQuery)
        .flatMapCompletable(
            entries -> {
              if (entries.size() == 1) {
                return saveLatest(tenantId, entityId, entries.get(0));
              }
              return Completable.complete();
            });
  }

  private Single<List<TsKvEntry>> findAllAsync(
      @SuppressWarnings("unused") TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
    if (query.getAggregation() == Aggregation.NONE) {
      return findAllAsyncWithLimit(entityId, query);
    } else {
      return Single.error(new UnsupportedOperationException("findAllAsync with aggregation"));
    }
  }

  private Single<List<TsKvEntry>> findAllAsyncWithLimit(EntityId entityId, ReadTsKvQuery query) {
    return Single.defer(
        () -> {
          int limit = query.getLimit() == null ? 1000 : query.getLimit();
          return Single.just(
              tsKvTunnel
                  .listWithLimit(
                      entityId.getId(),
                      entityId.getEntityType(),
                      query.getKeys(),
                      query.getStartTs(),
                      query.getEndTs(),
                      PageRequest.of(
                          0, limit, new Sort(Direction.fromString(query.getOrderBy()), "ts")))
                  .stream()
                  .map(KvDaoConverter::toTsKvEntry)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .collect(toList()));
        });
  }
}

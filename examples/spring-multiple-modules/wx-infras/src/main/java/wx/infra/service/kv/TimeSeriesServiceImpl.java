package wx.infra.service.kv;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import com.google.common.base.Strings;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.infra.kv.TsKvEntry;
import wx.common.data.infra.kv.query.DeleteTsKvQuery;
import wx.common.data.infra.kv.query.ReadTsKvQuery;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;

@Service
@Slf4j
public class TimeSeriesServiceImpl implements TimeSeriesService {

  private TimeSeriesDao timeSeriesDao;

  @Getter private TimeSeriesSetting timeSeriesSetting;

  public TimeSeriesServiceImpl(TimeSeriesDao timeSeriesDao, TimeSeriesSetting timeSeriesSetting) {
    this.timeSeriesDao = timeSeriesDao;
    this.timeSeriesSetting = timeSeriesSetting;
  }

  @Override
  public Flowable<TsKvEntry> findAll(TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
    TimeSeriesValidator.validateReadTsKvQuery(query, timeSeriesSetting.getMaxTsIntervals());
    return timeSeriesDao.findAllByQueryAsync(tenantId, entityId, query);
  }

  @Override
  public Flowable<TsKvEntry> findLatest(
      TenantId tenantId, EntityId entityId, Collection<String> keys) {
    keys.forEach(key -> checkArgument(!Strings.isNullOrEmpty(key), "Incorrect key %s", key));

    return Flowable.create(
        emitter -> {
          keys.forEach(
              key -> {
                Disposable ignored =
                    timeSeriesDao
                        .findLatest(tenantId, entityId, key)
                        .subscribe(v -> v.ifPresent(emitter::onNext));
              });
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  @Override
  public Single<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId) {
    return timeSeriesDao.findAllLatest(tenantId, entityId);
  }

  @Override
  public Completable save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
    return doSave(tenantId, entityId, tsKvEntry, 0L);
  }

  @Override
  public Completable save(
      TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntries, long ttl) {
    return Completable.concat(
        tsKvEntries.stream()
            .map(entry -> doSave(tenantId, entityId, entry, ttl))
            .collect(toList()));
  }

  @Override
  public Completable remove(
      TenantId tenantId, EntityId entityId, List<DeleteTsKvQuery> deleteTsKvQueries) {
    deleteTsKvQueries.forEach(TimeSeriesValidator::validateDeleteTsKvQuery);
    return Completable.concat(
        deleteTsKvQueries.stream()
            .map(deleteTsKvQuery -> doRemove(tenantId, entityId, deleteTsKvQuery))
            .collect(toList()));
  }

  /** 如果值和最新数据一致，将不保存 */
  private Completable doSave(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry, long ttl) {
    Optional<TsKvEntry> latest =
        timeSeriesDao.findLatest(tenantId, entityId, tsKvEntry.getKey()).blockingGet();
    if (latest.isPresent()) {
      if (Objects.equals(latest.get().getValue(), tsKvEntry.getValue())) {
        return Completable.complete();
      }
    }
    return Completable.concat(
        asList(
            timeSeriesDao.savePartition(
                tenantId, entityId, tsKvEntry.getTs(), tsKvEntry.getKey(), ttl),
            timeSeriesDao.saveLatest(tenantId, entityId, tsKvEntry),
            timeSeriesDao.save(tenantId, entityId, tsKvEntry, ttl)));
  }

  private Completable doRemove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
    List<Completable> completableList =
        query.getKeys().stream()
            .map(key -> timeSeriesDao.removeLatest(tenantId, entityId, query, key))
            .collect(toList());
    completableList.add(timeSeriesDao.remove(tenantId, entityId, query));
    completableList.add(timeSeriesDao.removePartition(tenantId, entityId, query));

    return Completable.concat(completableList);
  }
}

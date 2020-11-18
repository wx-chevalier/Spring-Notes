package wx.infra.common.persistence;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import io.reactivex.rxjava3.core.Single;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.domain.shared.IdBasedEntity;
import wx.domain.shared.IdBasedEntityRepository;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.shared.BaseDO;
import wx.infra.tunnel.db.shared.FieldNames;
import wx.infra.validator.EntityValidator;

/** 注意，这里范型使用 {@code LambdaQueryWrapper<D>} 进行查询会出错，现直接使用字段名查询 */
@Slf4j
public abstract class MyBatisIdBasedEntityRepository<
        Tunnel extends ServiceImpl<Mapper, D>,
        Mapper extends BaseMapper<D>,
        D extends BaseDO<D>,
        E extends IdBasedEntity<Id, E>,
        Id extends EntityId>
    implements IdBasedEntityRepository<Id, E> {

  @Getter(AccessLevel.PROTECTED)
  private Tunnel tunnel;

  @Getter(AccessLevel.PROTECTED)
  private Mapper mapper;

  @Setter(AccessLevel.PROTECTED)
  private EntityValidator<E, Id> validator;

  public MyBatisIdBasedEntityRepository(
      Tunnel tunnel, Mapper mapper, EntityValidator<E, Id> validator) {
    this.tunnel = tunnel;
    this.mapper = mapper;
    this.validator = validator;
  }

  public MyBatisIdBasedEntityRepository(Tunnel tunnel, Mapper mapper) {
    this(tunnel, mapper, new EntityValidator<E, Id>() {});
  }

  protected abstract AbstractConverter<E, D> getConverter();

  @Override
  public List<E> find(@NonNull TenantId tenantId) {
    log.debug("{} find entities of {}", getClass().getSimpleName(), tenantId);
    return tunnel
        .list(
            new QueryWrapper<D>()
                .eq(tenantId != TenantId.NULL_TENANT_ID, FieldNames.TENANT_ID, tenantId.getId())
                .isNull(FieldNames.DELETED_AT)
                .orderByDesc(FieldNames.ID))
        .stream()
        .map(getConverter()::convertFrom)
        .collect(Collectors.toList());
  }

  @Override
  public Single<Collection<E>> findByIds(
      @NonNull TenantId tenantId, Collection<Id> ids, boolean throwOnNotAllFound) {
    if (ids.size() == 0) {
      return Single.just(new ArrayList<>());
    }
    return Single.defer(
        () -> {
          List<D> data =
              tunnel.list(
                  new QueryWrapper<D>()
                      .eq(
                          tenantId != TenantId.NULL_TENANT_ID,
                          FieldNames.TENANT_ID,
                          tenantId.getId())
                      .in(FieldNames.ID, ids.stream().map(Id::getId).collect(Collectors.toSet()))
                      .orderByDesc(FieldNames.ID));
          if (throwOnNotAllFound && data.size() != ids.size()) {
            SetView<Long> absentIds =
                Sets.difference(
                    ids.stream().map(Id::getId).collect(Collectors.toSet()),
                    data.stream().map(D::getId).collect(Collectors.toSet()));
            String absentIdStr =
                ids.stream()
                    .filter(id -> absentIds.contains(id.getId()))
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            throw new NotFoundException("Entities not found: " + absentIdStr);
          }
          return Single.just(
              data.stream().map(getConverter()::convertFrom).collect(Collectors.toList()));
        });
  }

  @Override
  public Optional<E> findById(@NonNull TenantId tenantId, @NonNull Id id) {
    log.debug("{} find entity {} {}", getClass().getSimpleName(), tenantId, id);
    D one =
        tunnel.getOne(
            new QueryWrapper<D>()
                .eq(tenantId != TenantId.NULL_TENANT_ID, FieldNames.TENANT_ID, tenantId.getId())
                .eq(FieldNames.ID, id.getId())
                .isNull(FieldNames.DELETED_AT));
    return Optional.ofNullable(one).map(getConverter()::convertFrom);
  }

  @Override
  public E assertExists(@NonNull TenantId tenantId, @NonNull Id id) {
    return findById(tenantId, id).orElseThrow(() -> new NotFoundException("Not found: " + id));
  }

  @Override
  public boolean exists(@NonNull TenantId tenantId, @NonNull Id id) {
    log.debug("{} exists {} {}", getClass().getSimpleName(), tenantId, id);
    return 0
        != tunnel.count(
            new QueryWrapper<D>()
                .eq(tenantId != TenantId.NULL_TENANT_ID, FieldNames.TENANT_ID, tenantId.getId())
                .eq(FieldNames.ID, id.getId())
                .isNull(FieldNames.DELETED_AT));
  }

  @Override
  public E save(@NonNull TenantId tenantId, @NonNull E e) {
    log.debug("{} save {} {}", getClass().getSimpleName(), tenantId, e);
    validator.validate(e, v -> tenantId);
    D d = getConverter().convertTo(e);
    if (e.getId() == null) {
      tunnel.save(d);
      return getConverter().convertFrom(d);
    } else {
      if (!exists(tenantId, e.getId())) {
        throw new NotFoundException("Error updating, entity not found: " + e.getId());
      } else {
        tunnel.updateById(d);
        return getConverter().convertFrom(d);
      }
    }
  }

  @Override
  public boolean removeById(@NonNull TenantId tenantId, @NonNull Id id) {
    log.debug("{} remove {} {}", getClass().getSimpleName(), tenantId, id);
    if (!exists(tenantId, id)) {
      return false;
    } else {
      return tunnel.update(
          new UpdateWrapper<D>()
              .isNull(FieldNames.DELETED_AT)
              .eq(tenantId != TenantId.NULL_TENANT_ID, FieldNames.TENANT_ID, tenantId.getId())
              .eq(FieldNames.ID, id.getId())
              .set(FieldNames.DELETED_AT, LocalDateTime.now()));
    }
  }

  @Override
  public Optional<E> unsafeFindById(@NonNull Id id) {
    log.debug("{} unsafe find entity {}", getClass().getSimpleName(), id);
    D one =
        tunnel.getOne(
            new QueryWrapper<D>().eq(FieldNames.ID, id.getId()).isNull(FieldNames.DELETED_AT));
    return Optional.ofNullable(one).map(getConverter()::convertFrom);
  }
}

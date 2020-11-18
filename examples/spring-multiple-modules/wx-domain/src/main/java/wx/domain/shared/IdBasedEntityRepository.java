package wx.domain.shared;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;

/**
 * 基础 Repository，提供根据 Id 和租户 Id 进行的相关基础操作
 *
 * @param <E> 实体类型
 * @param <Id> 实体 Id 类型
 */
public interface IdBasedEntityRepository<Id extends EntityId, E extends IdBasedEntity<Id, E>> {

  List<E> find(@NonNull TenantId tenantId);

  Optional<E> findById(@NonNull TenantId tenantId, @NonNull Id id);

  E assertExists(@NonNull TenantId tenantId, @NonNull Id id);

  default Single<Collection<E>> findByIds(@NonNull TenantId tenantId, Collection<Id> ids) {
    return findByIds(tenantId, ids, true);
  }

  /** @param throwOnNotAllFound true -> 在 ids 中实体不是全部存在时抛出异常 */
  Single<Collection<E>> findByIds(
      @NonNull TenantId tenantId, Collection<Id> ids, boolean throwOnNotAllFound);

  boolean exists(@NonNull TenantId tenantId, @NonNull Id id);

  /**
   * @param tenantId TenantId
   * @param e {@code t.getId()} 不为空会新增，返回的 {@code T} 中包含 Id
   */
  E save(@NonNull TenantId tenantId, @NonNull E e);

  boolean removeById(@NonNull TenantId tenantId, @NonNull Id id);

  /** 根据 ID 查找实体，<b>注意无租户参数</b> */
  Optional<E> unsafeFindById(@NonNull Id id);
}

package wx.infra.tunnel.db.infra.kv;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import wx.common.data.shared.EntityType;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.kv.TsKvLatestMapper;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TsKvLatestTunnel extends ServiceImpl<TsKvLatestMapper, TsKvLatestDO> {
  /** @return true: 插入；false: 更新 */
  public boolean saveOrUpdate(TsKvLatestDO entity) {
    if (count(primaryKeyQ(entity.getEntityId(), entity.getEntityType(), entity.getKey())) == 0) {
      save(entity);
      return true;
    } else {
      super.update(
          entity, primaryKeyU(entity.getEntityId(), entity.getEntityType(), entity.getKey()));
      return false;
    }
  }

  public TsKvLatestDO getOneByCompositeKey(Long entityId, EntityType entityType, String key) {
    return getOne(primaryKeyQ(entityId, entityType, key));
  }

  public boolean removeByCompositeKey(Long entityId, EntityType entityType, String key) {
    return remove(primaryKeyQ(entityId, entityType, key));
  }

  public List<TsKvLatestDO> listByEntityIdAndEntityType(Long entityId, EntityType entityType) {
    return list(
        Helper.tsKvLatestQ()
            .eq(TsKvLatestDO::getEntityId, entityId)
            .eq(TsKvLatestDO::getEntityType, entityType));
  }

  private LambdaQueryWrapper<TsKvLatestDO> primaryKeyQ(
      Long entityId, EntityType entityType, String key) {
    return primaryKeyQ(entityId, entityType.name(), key);
  }

  private LambdaQueryWrapper<TsKvLatestDO> primaryKeyQ(
      Long entityId, String entityType, String key) {
    return Helper.tsKvLatestQ()
        .eq(TsKvLatestDO::getEntityId, entityId)
        .eq(TsKvLatestDO::getEntityType, entityType)
        .eq(TsKvLatestDO::getKey, key);
  }

  private LambdaUpdateWrapper<TsKvLatestDO> primaryKeyU(
      Long entityId, String entityType, String key) {
    return Helper.tsKvLatestU()
        .eq(TsKvLatestDO::getEntityId, entityId)
        .eq(TsKvLatestDO::getEntityType, entityType)
        .eq(TsKvLatestDO::getKey, key);
  }
}

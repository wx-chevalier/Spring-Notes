package wx.infra.tunnel.db.mapper.infra.kv;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import wx.common.data.shared.EntityType;
import wx.infra.tunnel.db.infra.kv.TsKvDO;

public interface TsKvMapper extends BaseMapper<TsKvDO> {

  String selectMaxString(
      @Param("entityId") Long entityId,
      @Param("entityType") EntityType entityType,
      @Param("entityKey") String entityKey,
      @Param("startTs") long startTs,
      @Param("endTs") long endTs);

  /** 获取指定资源的最新时间值 */
  Long getMaxTs(
      @Param("entityType") EntityType type,
      @Param("key") String key,
      @Param("entityId") Long entityId);
}

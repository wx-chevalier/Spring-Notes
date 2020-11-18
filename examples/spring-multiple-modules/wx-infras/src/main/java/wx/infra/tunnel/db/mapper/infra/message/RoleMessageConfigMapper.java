package wx.infra.tunnel.db.mapper.infra.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.infra.message.RoleMessageConfigDO;
import wx.infra.tunnel.db.infra.message.RoleMessageQueryDO;

@Component
public interface RoleMessageConfigMapper extends BaseMapper<RoleMessageConfigDO> {

  List<RoleMessageQueryDO> getConfig(
      @Param("tenantId") long tenantId, @Param("roleId") long roleId, @Param("appId") long appId);

  /**
   * 根据用户ID查询其消息通知发送的渠道
   *
   * @param tenantId 用户唯一标识
   * @return 用户发送的渠道集合
   */
  List<String> getSendChannelByTenantId(
      @Param("tenantId") Long tenantId, @Param("messageTypeId") Long messageTypeId);

  /** 获取某租户下发送指定类型的消息的目标用户 */
  List<Long> getTargetUsrIds(
      @Param("tenantId") Long tenantId, @Param("messageTypeId") Long messageTypeId);
}

package wx.infra.tunnel.db.infra.message;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_role_message_config")
public class RoleMessageConfigDO extends BaseDO<RoleMessageConfigDO> {

  private Long roleId;

  private Long messageTypeId;

  private String sendChannel;

  private Integer sendInterval;

  private Long creatorId;
}

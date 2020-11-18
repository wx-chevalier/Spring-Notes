package wx.infra.tunnel.db.infra.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleMessageQueryDO extends RoleMessageConfigDO {

  private String messageTypeName;

  private String messageTypeKey;

  private String messageTypeKind;
}

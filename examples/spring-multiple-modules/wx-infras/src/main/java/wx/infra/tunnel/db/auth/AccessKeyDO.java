package wx.infra.tunnel.db.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wx.common.data.shared.EntityType;

@TableName("auth_access_key")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessKeyDO {

  private long tenantId;

  private String key;

  private String secret;

  private EntityType entityType;

  private long entityId;
}

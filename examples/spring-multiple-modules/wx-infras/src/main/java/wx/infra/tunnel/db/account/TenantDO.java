package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("u_tenant")
public class TenantDO extends BaseDO<TenantDO> {

  private static final long serialVersionUID = 1L;

  private Long companyId;

  private String name;

  private String areaCode;
}

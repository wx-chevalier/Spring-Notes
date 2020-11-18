package wx.infra.tunnel.db.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_wx_application")
public class ApplicationDO extends BaseDO<ApplicationDO> {

  private String name;
}

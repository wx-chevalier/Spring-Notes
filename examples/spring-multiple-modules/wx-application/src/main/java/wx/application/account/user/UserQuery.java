package wx.application.account.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wx.common.data.page.PageNumBasedPageLink;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuery {
  private TenantId tenantId;

  private String searchText;

  private RoleId roleId;

  private PageNumBasedPageLink pageLink;
}

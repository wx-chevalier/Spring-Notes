package wx.application.account.user;

import java.util.Optional;
import org.springframework.data.domain.Page;
import wx.common.data.page.PageNumBasedPageLink;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Tenant;

public interface TenantQueryService {

  Page<TenantDetail> findAll(PageNumBasedPageLink link, String searchText, String location);

  Optional<TenantDetail> findById(TenantId tenantId);

  Optional<TenantDetail> save(
      Tenant tenant, UserId currentUserId, String username, String password);
}

package wx.application.account.user;

import io.reactivex.rxjava3.core.Single;
import java.util.*;
import org.springframework.data.domain.Page;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;

public interface UserQueryService {
  Page<User> findUsers(UserQuery query);

  Page<UserDetail> findUserDetails(UserQuery query);

  Single<List<UserDetail>> findUsers(TenantId tenantId);

  Single<Map<UserId, UserDetail>> findUserDetailsByIds(
      TenantId tenantId, Collection<UserId> userIds);

  UserDetail findUserDetail(TenantId tenantId, UserId userId);

  /** 获取所有租户下的租户管理员用户 */
  Set<User> findTenantAdmin(Set<TenantId> tenantIdStream);

  Optional<User> findTenantAdmin(TenantId tenantId);
}

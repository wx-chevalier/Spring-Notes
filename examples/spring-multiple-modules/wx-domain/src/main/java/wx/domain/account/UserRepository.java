package wx.domain.account;

import java.util.List;
import java.util.Optional;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntityRepository;

public interface UserRepository extends IdBasedEntityRepository<UserId, User> {

  /** TODO: 多租户相同用户名 */
  Optional<User> findById(UserId userId);

  /**
   * @param tenantId TenantId
   * @param username 可能为 username/phoneNumber/email
   */
  Optional<User> findByUsername(TenantId tenantId, String username);

  /**
   * TODO: 多租户相同用户名
   *
   * @param username 可能为 username/phoneNumber/email；当一个用户名在多个租户中同时出现，抛出异常
   */
  Optional<User> findByUsername(String username);

  /** 检查用户名是否存在 */
  boolean exists(String name);

  /** 查询某个租户的管理用户 */
  Optional<User> findAdmin(TenantId id);

  Optional<User> findByPhoneNumber(String phoneNumber);

  List<User> findAll(TenantId tenantId);
}

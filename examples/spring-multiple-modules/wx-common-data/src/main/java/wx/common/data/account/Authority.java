package wx.common.data.account;

import java.util.Optional;

public enum Authority {
  // 系统管理员
  SYS_ADMIN,
  // 租户管理员
  TENANT_ADMIN,
  // 租户用户
  TENANT_USER,
  // 租户网关
  TENANT_GATEWAY;

  public static Optional<Authority> parse(String value) {
    Authority authority = null;
    if (value != null && value.length() != 0) {
      for (Authority current : Authority.values()) {
        if (current.name().equalsIgnoreCase(value)) {
          authority = current;
          break;
        }
      }
    }
    return Optional.ofNullable(authority);
  }
}

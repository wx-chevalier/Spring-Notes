package wx.constants;

import java.util.Optional;
import lombok.NonNull;

/** @author wxyyxc1992 */
public enum UserRole {

  /** 管理员 */
  ADMIN;

  static final String ROLE_PREFIX = "ROLE_";

  public static UserRole ofString(String s) {
    for (UserRole value : values()) {
      if (value.name().equals(s)) {
        return value;
      }
    }
    throw new RuntimeException(String.format("Unknown role: %s", s));
  }

  public static Optional<UserRole> fromRoleString(@NonNull String roleString) {
    if (roleString.startsWith(ROLE_PREFIX)) {
      return Optional.of(UserRole.valueOf(roleString.substring(ROLE_PREFIX.length())));
    } else {
      return Optional.empty();
    }
  }

  public String toRoleString() {
    return ROLE_PREFIX + name();
  }
}

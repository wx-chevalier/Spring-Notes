package wx.common.data.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 权限状态枚举 */
@AllArgsConstructor
public enum PermissionStatusEnum {
  ENABLE("启用"),
  DISABLE("禁用");

  @Getter String desc;
}

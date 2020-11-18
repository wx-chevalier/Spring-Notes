package wx.common.data.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 角色状态枚举 */
@AllArgsConstructor
public enum RoleStatusEnum {
  ENABLE("启用"),
  DISABLE("禁用");

  @Getter String desc;
}

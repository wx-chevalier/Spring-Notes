package wx.common.data.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PermissionTypeEnum {
  CATALOG("目录权限"),
  MENU("菜单权限"),
  BUTTON("按钮权限");

  @Getter String permissionRemark;
}

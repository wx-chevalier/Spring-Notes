package wx.infra.tunnel.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import wx.infra.tunnel.db.account.*;
import wx.infra.tunnel.db.infra.kv.TsKvDO;
import wx.infra.tunnel.db.infra.kv.TsKvLatestDO;

public class Helper {
  public static LambdaQueryWrapper<TenantDO> tenantQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<RolePermissionRelationDO> rolePermissionRelationQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<RolePermissionRelationDO> rolePermissionRelationC() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<UserDO> userQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<UserCredentialsDO> userCredentialsQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<RoleDO> roleQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<PermissionDO> permissionQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<UserPermissionRelationDO> userPermissionRelationQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<UserRoleRelationDO> userRoleRelationQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<TsKvDO> tsKvQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaQueryWrapper<TsKvLatestDO> tsKvLatestQ() {
    return new LambdaQueryWrapper<>();
  }

  public static LambdaUpdateWrapper<TsKvLatestDO> tsKvLatestU() {
    return new LambdaUpdateWrapper<>();
  }

  public static <T> LambdaQueryWrapper<T> getQueryWrapper(Class<T> clazz) {
    return new LambdaQueryWrapper<T>();
  }

  public static <T> LambdaUpdateWrapper<T> getUpdateWrapper(Class<T> clazz) {
    return new LambdaUpdateWrapper<T>();
  }
}

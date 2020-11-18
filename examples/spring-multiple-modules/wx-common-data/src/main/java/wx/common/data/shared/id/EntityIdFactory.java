package wx.common.data.shared.id;

import wx.common.data.shared.EntityType;

public class EntityIdFactory {

  public static Class[] entityIdClasses =
      new Class[] {
        TagId.class,
        FileId.class,
        UserId.class,
        UserCredentialsId.class,
        TenantId.class,
        ApplicationId.class,
        TaskId.class,
        VerificationCodeId.class,
        CompanyId.class,
        PermissionSettingId.class
      };

  public static EntityId getByTypeAndId(String type, String strId) {
    return getByTypeAndId(EntityType.valueOf(type), Long.parseUnsignedLong(strId));
  }

  public static EntityId getByTypeAndId(EntityType type, String strId) {
    return getByTypeAndId(type, Long.parseUnsignedLong(strId));
  }

  public static EntityId getByTypeAndId(EntityType type, Long id) {
    switch (type) {
      case TAG:
        return new TagId(id);
      case FILE:
        return new FileId(id);
      case USER:
        return new UserId(id);
      case USER_CREDENTIALS:
        return new UserCredentialsId(id);
      case TENANT:
        return new TenantId(id);
      case APPLICATION:
        return new ApplicationId(id);
      case TASK:
        return new TaskId(id);
      case VERIFICATION_CODE:
        return new VerificationCodeId(id);
      case COMPANY:
        return new CompanyId(id);
      case PERMISSION_SETTING:
        return new PermissionSettingId(id);
      default:
        throw new IllegalArgumentException("EntityType " + type + " is not supported!");
    }
  }
}

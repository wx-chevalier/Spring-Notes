package wx.common.data.infra.notice;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 通知类型 */
@AllArgsConstructor
public enum NoticeType {
  // 设备相关消息类型
  DEVICE_ERROR("设备就绪异常"),
  DEVICE_OFFLINE("设备离线"),
  DEVICE_EXPIRE("设备到期"),
  DEVICE_RECONNECT("设备重新连接"),
  DEVICE_DISABLED("设备被禁用"),
  DEVICE_WAIT_CONFIG("设备等待配置"),

  // 账户配置相关
  BIND_EMAIL("绑定邮箱地址"),
  BIND_PHONE("绑定手机号码"),
  RESET("重置账户密码"),
  SMS_LOGIN("手机登录验证码"),
  EMAIL_LOGIN("邮箱登录验证码"),

  // 材料变动相关
  MATERIAL_CREATE("创建材料"),
  MATERIAL_UPDATE("更新材料"),
  MATERIAL_DELETE("移除材料"),

  // 工艺包变动
  UTK_BP_CREATE("创建租户工艺包"),
  UTK_BP_EDIT("编辑租户工艺包"),
  UTK_BP_DELETE("删除租户工艺包"),
  BASE_BP_UPDATE("更新基础工艺包"),

  // 工单相关通知
  ORDER_PRINT_START("工单打印开始"),
  ORDER_PRINT_WILL_COMPLETE("工单打印即将完成"),
  ORDER_PRINT_COMPLETE("工单打印完成"),
  ORDER_PRINT_EXCEPTION("工单打印异常"),
  ORDER_PICK_UP_COMPLETE("工单取件完成"),
  ORDER_SCHEDULE_CANCEL("工单取消排产"),
  ORDER_PRINT_RESUME("工单恢复打印"),
  ORDER_PRINT_PAUSE("工单暂停打印"),
  ORDER_PRINT_STOP("工单终止打印"),
  ORDER_PRINT_CANCEL("工单取消打印"),

  ORDER_BAD_PART_FEEDBACK("工单坏件反馈"),
  ORDER_BAD_PART_FEEDBACK_HANDLE("工单坏件反馈处理");

  @Getter private String desc;

  /** 获取验证码通知 */
  public static List<NoticeType> getAllValidateCodeNotice() {
    return Arrays.asList(BIND_EMAIL, BIND_PHONE, RESET);
  }

  public static NoticeType getByValue(String value) {
    return Arrays.stream(values())
        .filter(type -> Objects.equals(type.name(), value))
        .findAny()
        .orElse(null);
  }
}

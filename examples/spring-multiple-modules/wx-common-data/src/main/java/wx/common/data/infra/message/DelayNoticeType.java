package wx.common.data.infra.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 延时通知的类型 */
@AllArgsConstructor
public enum DelayNoticeType {
  DEVICE_POWER_ERROR("设备功率异常"),
  DEVICE_INIT_ERROR("设备初始化异常"),
  LEVEL_ERROR("液位异常"),
  DEVICE_OFFLINE("设备离线"),
  DEVICE_OVERDUE("设备到期"),
  DEVICE_RECONNECT("设备重新连接");

  @Getter private String desc;
}

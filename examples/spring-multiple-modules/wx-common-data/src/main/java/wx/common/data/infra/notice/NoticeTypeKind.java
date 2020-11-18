package wx.common.data.infra.notice;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 通知类型的种类 */
@AllArgsConstructor
public enum NoticeTypeKind {
  TASK("任务"),
  ERROR("错误"),
  WARNING("告警"),
  CONFIG("配置"),
  LOGIN("登录"),
  NOTICE("通知");

  @Getter String desc;

  public static List<NoticeTypeKind> getSiteType() {
    return Arrays.asList(TASK, ERROR, WARNING);
  }
}

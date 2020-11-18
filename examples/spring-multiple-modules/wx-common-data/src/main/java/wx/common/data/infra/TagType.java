package wx.common.data.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TagType {
  DATETIME("绝对时间"),
  PRINT_REQUIRED("打印要求"),
  BAD_FEEDBACK("坏件反馈"),
  STOP_REASON("停止打印原因"),
  MATERIAL_FEATURE("材料特性");

  @Getter String desc;
}

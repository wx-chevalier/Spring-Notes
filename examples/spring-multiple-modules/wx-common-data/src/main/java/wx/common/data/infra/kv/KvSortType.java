package wx.common.data.infra.kv;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum KvSortType {
  ASC("升序"),
  DESC("降序");

  @Getter String desc;
}

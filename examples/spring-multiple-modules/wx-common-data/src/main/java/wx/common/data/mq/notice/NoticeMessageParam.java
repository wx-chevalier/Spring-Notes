package wx.common.data.mq.notice;

import java.io.Serializable;
import java.util.Map;

/** 通知消息模板参数 */
public interface NoticeMessageParam extends Serializable {

  /** 参数汇总 */
  Map<String, Object> toMap();
}

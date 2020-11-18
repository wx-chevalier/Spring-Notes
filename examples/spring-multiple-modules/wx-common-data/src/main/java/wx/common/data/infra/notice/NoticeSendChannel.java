package wx.common.data.infra.notice;

import com.google.common.collect.Sets;
import java.util.Set;

/** 通知发送渠道 */
public enum NoticeSendChannel {
  PHONE, // 手机
  EMAIL, // 电子邮箱
  SMS, // 短消息
  WECHAT, // 微信
  SITE // 站内信
;

  /** 目前支持的发送消息渠道 */
  public static Set<NoticeSendChannel> supportSendChannel() {
    return Sets.newHashSet(EMAIL, SITE, WECHAT);
  }
}

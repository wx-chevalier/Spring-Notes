package wx.domain.wechat;

import java.time.LocalDateTime;
import lombok.Data;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.utils.DateTimeUtils;

@Data
public class WechatUser {

  private Long id;

  private UserId userId;

  private Integer subscribe;

  private String openId;

  private String nickName;

  private Integer sex;

  private String language;

  private String city;

  private String province;

  private String country;

  private String headImgUrl;

  private LocalDateTime subscribeTime;

  private LocalDateTime unsubscribeTime;

  private String unionId;

  private String subscribeScene;

  private TenantId tenantId;

  public WechatUser setSubscribeTime(Long subscribeTime) {
    if (subscribeTime == null) {
      return this;
    }
    this.subscribeTime = DateTimeUtils.fromTimestamp(subscribeTime);
    return this;
  }

  public WechatUser setUnsubscribeTime(Long unsubscribeTime) {
    if (subscribeTime == null) {
      return this;
    }
    this.unsubscribeTime = DateTimeUtils.fromTimestamp(unsubscribeTime);
    return this;
  }
}

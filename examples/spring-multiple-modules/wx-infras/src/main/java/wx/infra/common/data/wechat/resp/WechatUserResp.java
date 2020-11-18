package wx.infra.common.data.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WechatUserResp {

  @JsonProperty("errorcode")
  private Long errorCode;

  @JsonProperty("openid")
  private String openId;

  @JsonProperty("nickname")
  private String nickName;

  private Integer sex;

  private String language;

  private String city;

  private String province;

  private String country;

  @JsonProperty("headimgurl")
  private String headImgUrl;

  @JsonProperty("subscribe_time")
  private Long subscribeTime;

  @JsonProperty("unionid")
  private String unionId;

  @JsonProperty("subscribe_scene")
  private String subscribeScene;
}

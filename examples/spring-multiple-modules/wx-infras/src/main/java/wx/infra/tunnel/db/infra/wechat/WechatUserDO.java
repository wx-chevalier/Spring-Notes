package wx.infra.tunnel.db.infra.wechat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("u_user_wechat")
public class WechatUserDO {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String openId;

  private String unionId;

  private Long userId;

  private String nickName;

  private Integer sex;

  private String language;

  private String city;

  private String province;

  private String country;

  private String headImgUrl;

  private LocalDateTime subscribeTime;

  private LocalDateTime unsubscribeTime;

  private String subscribeScene;

  private Long tenantId;
}

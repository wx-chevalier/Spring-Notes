package wx.common.data.mq.ali;

import java.io.Serializable;
import lombok.Data;

@Data
public class DingTalkMessage implements Serializable {

  private static final long serialVersionUID = 5166279968208820629L;

  private static final String ACCESS_TOKEN =
      "136f51b999f1910965cb92c19c1edea07fdaa251a639c82d1b4ef7771a5a3f2f";

  private String accessToken = ACCESS_TOKEN;

  private String message;

  public DingTalkMessage(String message) {
    this.message = message;
  }
}

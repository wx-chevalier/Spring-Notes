package wx.api.rest.common.message.dto;

import io.swagger.annotations.ApiModel;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import wx.common.data.infra.notice.NoticeSendChannel;

@Data
@ApiModel("消息查询命令")
public class SendMessageReq {

  @NotEmpty(message = "发送目标用户不能为空")
  Set<String> userIds;

  @NotEmpty(message = "发送途径不能为空")
  Set<NoticeSendChannel> channels;

  Map<String, String> param;
}

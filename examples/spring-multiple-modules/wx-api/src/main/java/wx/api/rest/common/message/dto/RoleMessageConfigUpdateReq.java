package wx.api.rest.common.message.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeTypeKind;

@Data
@ApiModel("角色消息推送配置请求对象")
public class RoleMessageConfigUpdateReq {

  @ApiModelProperty("发送消息的类型")
  List<String> messageTypeIds;

  @ApiModelProperty("发送渠道集合")
  List<NoticeSendChannel> sendChannelList;

  @ApiModelProperty("消息类型的种类")
  @NotNull(message = "消息的种类不能为空")
  private NoticeTypeKind kind;

  @NotNull(message = "推送时间间隔不能为空")
  @ApiModelProperty("推送时间间隔(单位：分钟)")
  private Integer interval;
}

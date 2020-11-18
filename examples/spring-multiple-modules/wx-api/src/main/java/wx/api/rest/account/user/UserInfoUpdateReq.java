package wx.api.rest.account.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户名称更新请求")
public class UserInfoUpdateReq {

  @ApiModelProperty("用户昵称")
  private String nickName;

  @ApiModelProperty("用户头像文件ID")
  private Long fileId;

  @ApiModelProperty("备注信息")
  private String remark;
}

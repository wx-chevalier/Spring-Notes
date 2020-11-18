package wx.api.rest.common.verificationcode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import wx.common.data.infra.notice.NoticeSendChannel;

@Data
@ApiModel("验证码发送请求")
public class VerificationCodeRequest {

  @ApiModelProperty("验证码发送目标（手机号/邮箱）")
  private String sendDst;

  @ApiModelProperty("用户密码")
  private String password;

  @ApiModelProperty("验证码发送媒介")
  private NoticeSendChannel channel;
}

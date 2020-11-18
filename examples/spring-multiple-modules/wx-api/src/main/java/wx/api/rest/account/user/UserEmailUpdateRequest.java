package wx.api.rest.account.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import lombok.Data;
import wx.common.data.common.ValidatorPatterns;

@Data
@ApiModel("用户更换电子邮箱绑定命令")
public class UserEmailUpdateRequest {
  @Pattern(regexp = ValidatorPatterns.EMAIL, message = "操作失败，电子邮箱地址不正确")
  @ApiModelProperty("更换的电子邮箱")
  private String email;

  @ApiModelProperty("当前密码")
  private String password;
}

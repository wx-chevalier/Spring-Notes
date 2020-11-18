package wx.api.rest.account.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;
import wx.common.data.common.ValidatorPatterns;

@Data
@ApiModel("用户更换密码命令")
public class UserPasswordUpdateRequest {
  @NotBlank
  @Pattern(regexp = ValidatorPatterns.PASSWORD, message = "密码必须包含大小写字母以及数字，长度6-20位")
  @ApiModelProperty("新密码")
  private String newPassword;

  @Pattern(regexp = ValidatorPatterns.PASSWORD, message = "操作失败,密码错误")
  @ApiModelProperty("当前密码")
  private String currentPassword;
}

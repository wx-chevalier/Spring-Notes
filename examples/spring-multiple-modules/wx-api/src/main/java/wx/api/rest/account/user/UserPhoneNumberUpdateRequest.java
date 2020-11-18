package wx.api.rest.account.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import lombok.Data;
import wx.common.data.common.ValidatorPatterns;

@Data
@ApiModel("用户更换手机绑定命令")
public class UserPhoneNumberUpdateRequest {

  @Pattern(regexp = ValidatorPatterns.PHONE_NUMBER, message = "操作失败，手机号码格式不正确")
  @ApiModelProperty("更换的手机号码")
  private String phoneNumber;

  @ApiModelProperty("验证码")
  private String verificationCode;

  @Pattern(regexp = ValidatorPatterns.PASSWORD, message = "密码错误")
  @ApiModelProperty("当前密码")
  private String password;
}

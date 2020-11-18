package wx.api.rest.account.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import wx.common.data.common.ValidatorPatterns;

@Data
@ApiModel("用户重置")
public class UserPasswordResetRequest {
  @NotBlank
  @Pattern(regexp = ValidatorPatterns.PASSWORD, message = "密码必须包含大小写字母以及数字，长度6-12位")
  String password;

  @Pattern(regexp = ValidatorPatterns.PHONE_NUMBER, message = "手机号码格式不正确")
  String phoneNumber;

  @ApiModelProperty("username/phoneNumber/email")
  String username;

  @Size(min = 4, message = "验证码错误")
  @Deprecated
  String validateCode;

  @Size(min = 4, message = "验证码错误")
  String verificationCode;
}

package wx.api.rest.account.user;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import wx.common.data.account.Authority;
import wx.common.data.common.ValidatorPatterns;

@Data
public class UserUpdateRequest {

  @NotBlank
  @Pattern(regexp = ValidatorPatterns.USER_NAME, message = "用户名只允许大小写字母、数据以及中文，长度4-20位")
  private String username;

  @ApiModelProperty(hidden = true)
  private Authority authority;

  @NotBlank
  @Pattern(regexp = ValidatorPatterns.PASSWORD, message = "密码必须包含大小写字母以及数字，长度6-12位")
  private String password;

  @Size(max = 20, message = "用户姓名不能超出20个字")
  @ApiModelProperty("用户姓名")
  private String nickname;

  @NotEmpty(message = "用户角色不能为空")
  @ApiModelProperty("用户角色ID列表")
  private List<String> roleIds;

  @ApiModelProperty("备注")
  private String remark;

  @ApiModelProperty(value = "手机号码", notes = "非必填")
  private String phoneNumber;

  @ApiModelProperty(value = "电子邮箱", notes = "非必填")
  private String email;

  @ApiModelProperty(value = "头像文件ID", notes = "非必填")
  private String avatarFileId;
}

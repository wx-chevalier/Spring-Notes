package wx.api.rest.account.accesscontrol;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;
import wx.common.data.shared.id.*;
import wx.domain.account.Role;

@Data
public class RoleAddRequest {

  @ApiModelProperty("[必传]角色名称")
  @NotBlank
  private String name;

  @ApiModelProperty(value = "[可选]租户ID", notes = "不提供的话，选择当前登录的租户ID")
  @NotBlank
  private String tenantId;

  @ApiModelProperty(value = "[必选]角色昵称")
  @NotBlank
  private String nickname;

  @ApiModelProperty(value = "[可选]是否禁用", notes = "默认禁用", hidden = true)
  private boolean disabled = false;

  public Role toRole() {
    if (StringUtils.hasText(tenantId)) {
      return new Role(name, new TenantId(tenantId), nickname, disabled);
    } else {
      return new Role(name, null, nickname, disabled);
    }
  }
}

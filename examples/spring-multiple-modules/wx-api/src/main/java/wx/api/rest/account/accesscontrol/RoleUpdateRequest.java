package wx.api.rest.account.accesscontrol;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;
import wx.common.data.shared.id.*;
import wx.domain.account.Role;

@Data
public class RoleUpdateRequest {

  @NotNull(message = "角色ID不能为空")
  private String id;

  @ApiModelProperty(value = "[可选]租户ID", notes = "不提供的话，选择当前登录的租户ID")
  @NotBlank
  private String tenantId;

  @ApiModelProperty(value = "[必选]角色昵称")
  @NotBlank
  private String nickname;

  @ApiModelProperty(value = "[可选]是否禁用", notes = "默认启用", hidden = true)
  private boolean disabled = false;

  public Role toRole() {
    // 角色名称不允许更改
    if (StringUtils.hasText(tenantId)) {
      return new Role(null, new TenantId(tenantId), nickname, disabled);
    } else {
      return new Role(null, null, nickname, disabled);
    }
  }
}

package wx.api.rest.account.tenant;

import static wx.api.rest.shared.dto.envelope.ResponseEnvelope.createOk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.application.account.user.UserDetail;

@RestController
@RequestMapping("/tenant")
@Api(tags = "🏫 租户相关接口")
public class TenantController extends BaseController {

  @GetMapping("/user")
  @ApiOperation(value = "获取当前用户所属租户下所有用户")
  public ResponseEnvelope<List<UserDetail>> getAllUser() {
    return createOk(userQueryService.findUsers(getTenantId()).blockingGet());
  }
}

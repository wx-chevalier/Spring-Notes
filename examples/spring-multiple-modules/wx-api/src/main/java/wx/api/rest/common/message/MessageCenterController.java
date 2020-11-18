package wx.api.rest.common.message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import wx.api.rest.common.message.converter.RoleMessageConfigConverter;
import wx.api.rest.common.message.dto.RoleMessageConfigUpdateReq;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.domain.infra.message.MessageType;
import wx.domain.infra.message.RoleMessageConfig;

@RestController
@RequestMapping("/message_center")
@Api(tags = "📩 消息中心相关接口")
public class MessageCenterController extends BaseController {

  @ApiOperation(value = "获取所有的消息类型", notes = "自行按照kind字段分组，TASK: 任务类 ERROR：错误类 WARNING: 警告类")
  @GetMapping("/message_type")
  public ResponseEnvelope<List<MessageType>> getAllMessageType(String appId) {
    List<MessageType> map = messageTypeQueryService.getByAppId(new ApplicationId(appId));
    return ResponseEnvelope.createOk(map);
  }

  @ApiOperation(value = "更新角色的消息推送配置信息", notes = "全量更新,会逻辑删除原配置")
  @PostMapping("/config")
  public ResponseEnvelope update(
      @RequestParam String roleId, @RequestBody @Valid List<RoleMessageConfigUpdateReq> reqs) {
    TenantId tenantId = getTenantId();
    UserId userId = getCurrentUserId();

    List<RoleMessageConfig> configListList =
        reqs.stream()
            .map(RoleMessageConfigConverter::toRoleMessageConfig)
            .map(config -> config.setTenantId(tenantId).setCreatorId(userId))
            .collect(Collectors.toList());
    roleMessageConfigCommandService.update(tenantId, userId, new RoleId(roleId), configListList);
    return ResponseEnvelope.createOk();
  }
}

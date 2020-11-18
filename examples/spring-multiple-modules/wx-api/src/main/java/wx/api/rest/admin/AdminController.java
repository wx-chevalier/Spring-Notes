package wx.api.rest.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.application.admin.config.FileTemplateConfig;

@Api(tags = "系统管理")
@RestController("/admin")
public class AdminController extends BaseController {

  @GetMapping("/file/template")
  @ApiOperation("文件模板信息")
  public ResponseEnvelope<FileTemplateConfig> fileTemplate() {
    return ResponseEnvelope.createOk(adminConfigQueryService.getTemplateConfig(getTenantId()));
  }
}

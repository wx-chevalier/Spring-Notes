package wx.api.rest.admin.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.converter.PageConverter;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.application.account.user.TenantDetail;
import wx.common.data.page.PageNumBasedPageLink;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Tenant;
import wx.infra.common.exception.NotFoundException;

@Api(tags = "【管理端】租户管理")
@RequestMapping("/admin/tenant")
@RestController
public class AdminTenantController extends BaseController {

  @GetMapping("/list")
  @ApiOperation("获取全部的租户列表")
  public ResponseEnvelope<List<TenantDetail>> list(
      Integer pageSize, Integer pageNum, String searchText, String areaCode) {
    return PageConverter.toResponseEntity(
        tenantQueryService.findAll(
            new PageNumBasedPageLink(pageNum, pageSize), searchText, areaCode));
  }

  @PostMapping
  @ApiOperation("新增租户")
  public ResponseEnvelope<TenantDetail> create(@RequestBody AddTenantRequest req) {
    Tenant tenant = new Tenant(req.getName(), req.getAreaCode());
    return ResponseEnvelope.createOk(
        tenantQueryService
            .save(
                tenant,
                new UserId(1L),
                req.getTenantAdminUsername(),
                req.getTenantAdminUserPassword())
            .orElseThrow(() -> new NotFoundException("创建租户失败")));
  }
}

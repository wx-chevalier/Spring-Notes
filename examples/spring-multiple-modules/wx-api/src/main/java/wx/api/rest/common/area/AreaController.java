package wx.api.rest.common.area;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.domain.infra.area.Area;

@RestController
@Api(tags = "行政区域划分")
@RequestMapping("/area")
public class AreaController extends BaseController {

  @GetMapping
  @ApiOperation("获取中国区域行政类表")
  public ResponseEnvelope<List<Area>> getByCode(
      @RequestParam(required = false, defaultValue = "0") String parentCode) {
    return ResponseEnvelope.createOk(this.areaQueryService.getByParentCode(parentCode));
  }
}

package wx.controller.sys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import wx.dto.sys.memory.OsInfo;
import wx.service.sys.OshiService;

@RestController
@RequestMapping("/oshi")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class OshiController {
  private OshiService oshiService;

  public OshiController(OshiService oshiService) {
    this.oshiService = oshiService;
  }

  @PostMapping("/osInfo")
  public DeferredResult<OsInfo> ps() throws InterruptedException {

    DeferredResult<OsInfo> deferredResult = new DeferredResult<>();

    oshiService
        .getOsInfo()
        .whenComplete(
            ((osInfo, throwable) -> {
              if (throwable != null) {
                log.warn(throwable.getMessage(), throwable);
                deferredResult.setResult(osInfo);
              } else {
                deferredResult.setResult(osInfo);
              }
            }));

    return deferredResult;
  }
}

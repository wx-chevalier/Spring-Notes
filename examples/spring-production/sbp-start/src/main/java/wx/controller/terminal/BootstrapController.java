package wx.controller.terminal;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.service.sys.BashService;

/** 包含设备初始化相关的逻辑 */
@RestController
@RequestMapping("/bootstrap")
@PreAuthorize("hasRole('ADMIN')")
public class BootstrapController {

  @Autowired BashService bashService;

  @PostMapping("/install")
  public void install() {}

  @PostMapping("/uninstall")
  @ApiOperation(value = "DEV ONLY")
  public void uninstall() {}

  @PostMapping("/restart")
  public ResponseEntity<Boolean> restart() {
    boolean resp = bashService.execScript("reboot");

    if (resp) {
      return ResponseEntity.ok(true);
    } else {
      return ResponseEntity.ok(false);
    }
  }
}

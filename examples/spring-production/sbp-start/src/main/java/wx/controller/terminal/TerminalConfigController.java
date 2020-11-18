package wx.controller.terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.domain.terminal.config.TerminalConfig;

@RestController
@RequestMapping("/terminal")
@PreAuthorize("hasRole('ADMIN')")
public class TerminalConfigController {
  @Autowired TerminalConfig terminalConfig;

  /** 获取当前的设备信息 */
  @PostMapping("/info")
  public TerminalConfig.TerminalInfo.Info info() {
    return this.terminalConfig.getTerminal().getInfo();
  }
}

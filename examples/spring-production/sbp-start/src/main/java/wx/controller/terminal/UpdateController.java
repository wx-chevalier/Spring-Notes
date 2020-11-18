package wx.controller.terminal;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wx.context.properties.ApplicationProperty;
import wx.controller.exceptions.EntityNotFoundException;
import wx.domain.terminal.config.TerminalConfig;
import wx.service.terminal.update.UpdateService;

@RestController
@RequestMapping("/system-update")
@PreAuthorize("hasRole('ADMIN')")
public class UpdateController {
  @Autowired private ApplicationProperty applicationProperty;

  @Autowired private UpdateService updateService;

  @Autowired private TerminalConfig terminalConfig;

  /** 获取当前的版本更新信息 */
  @PostMapping("/versions")
  public String versions() throws IOException {
    File file = new File(terminalConfig.getAgent().getVersions());
    String data = FileUtils.readFileToString(file, "UTF-8");

    return data;
  }

  @PostMapping("/upload")
  public void update(@RequestParam("filename") String fileName) {
    File updateFile = new File(applicationProperty.getUserUploadDir(), fileName);
    if (!updateFile.exists()) {
      throw new EntityNotFoundException("更新文件不存在");
    }
    updateService.update(updateFile);
  }
}

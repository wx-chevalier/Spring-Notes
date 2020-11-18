package wx.service.terminal.update.impl;

import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Service;
import wx.domain.update.WsatUpdatePackage;
import wx.service.exceptions.UpdateException;
import wx.service.terminal.update.UpdateService;

@Service
public class UpdateServiceImpl implements UpdateService {

  @Override
  public void update(File updateFile) {
    WsatUpdatePackage wsatUpdatePackage;
    try {
      wsatUpdatePackage = WsatUpdatePackage.loadUpdatePackage(updateFile);
    } catch (IOException e) {
      throw new UpdateException("更新包加载出错", e);
    }
    try {
      wsatUpdatePackage.doUpdate();
    } catch (Exception e) {
      throw new UpdateException("更新过程发生异常", e);
    }
  }
}

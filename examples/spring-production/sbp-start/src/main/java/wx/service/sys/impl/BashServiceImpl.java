package wx.service.sys.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.service.sys.BashService;

@Slf4j
@Service
public class BashServiceImpl implements BashService {
  @Override
  public boolean execScript(String command) {
    try {

      Process p = Runtime.getRuntime().exec(command);

      System.out.println(command);

      InputStream is = p.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      p.waitFor();

      if (p.exitValue() != 0) {
        // 说明命令执行失败，可以进入到错误处理步骤中
        String s = null;
        while ((s = reader.readLine()) != null) {
          System.out.println(s);
        }

        return false;
      }
    } catch (IOException | InterruptedException e) {

      log.error(">>>execScript fail>>>" + command + ">>>" + e.getMessage());

      return false;
    }

    return true;
  }
}

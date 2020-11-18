package wx.infra.tunnel.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.springframework.stereotype.Component;
import wx.infra.common.exception.NotFoundException;

@Slf4j
@Component
public class ApplicationConfigTunnel {

  public String getApplicationData() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("config/applicationList.json");
    if (Objects.isNull(resourceAsStream)) {
      throw new NotFoundException("未能正确的读取配置文件");
    }
    Scanner scanner = new Scanner(resourceAsStream, "UTF-8");
    return scanner.useDelimiter("\\A").next();
  }
}

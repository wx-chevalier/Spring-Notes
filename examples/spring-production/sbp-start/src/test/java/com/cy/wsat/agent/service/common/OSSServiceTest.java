package wx.service.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wx.Application;
import wx.service.common.impl.OSSServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OSSServiceTest {
  @Autowired OSSServiceImpl ossService;

  @Test
  public void testGetVersionsIndexWithoutException() {
    System.out.println(ossService.getWASTVersionInfo());
  }
}

package wx.service.common.impl;

import com.github.dockerjava.core.DockerClientBuilder;
import org.junit.jupiter.api.Test;
import wx.dto.docker.DockerPSArgument;
import wx.service.sys.impl.DockerServiceImpl;

class DockerServiceImplTest {
  @Test
  void testPs() {
    System.out.println(
        new DockerServiceImpl(DockerClientBuilder.getInstance().build())
            .ps(new DockerPSArgument().setShowAll(true)));
  }

  @Test
  void testInspect() {
    System.out.println(
        new DockerServiceImpl(DockerClientBuilder.getInstance().build()).inspect("968af644d55"));
  }
}

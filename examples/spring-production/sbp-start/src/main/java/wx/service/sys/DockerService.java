package wx.service.sys;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import java.util.List;
import org.springframework.web.context.request.async.DeferredResult;
import wx.dto.docker.ContainerStatus;
import wx.dto.docker.DockerPSArgument;

public interface DockerService {
  /** Docker info */
  Info info();

  List<Image> images();

  /** Docker ps */
  List<Container> ps(DockerPSArgument argument);

  /** Docker status */
  void status(String containerId, DeferredResult<ContainerStatus> deferredResult);

  /** Docker inspect */
  InspectContainerResponse inspect(String containerId);
}

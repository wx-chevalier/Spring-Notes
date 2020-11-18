package wx.controller.sys;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import wx.dto.docker.ContainerStatus;
import wx.dto.docker.DockerPSArgument;
import wx.service.sys.DockerService;

@RestController
@RequestMapping("/docker")
@PreAuthorize("hasRole('ADMIN')")
public class DockerController {
  private DockerService dockerService;

  public DockerController(DockerService dockerService) {
    this.dockerService = dockerService;
  }

  @PostMapping("/ps")
  public List<Container> ps(@RequestBody DockerPSArgument argument) {
    return dockerService.ps(argument);
  }

  @PostMapping("/images")
  public List<Image> images() {
    return dockerService.images();
  }

  @PostMapping("/status/{containerId}")
  public DeferredResult<ContainerStatus> status(@PathVariable("containerId") String containerId) {
    DeferredResult<ContainerStatus> deferredResult = new DeferredResult<ContainerStatus>();

    this.dockerService.status(containerId, deferredResult);

    return deferredResult;
  }

  @PostMapping("/inspect/{containerId}")
  public InspectContainerResponse inspect(@PathVariable("containerId") String containerId) {
    return dockerService.inspect(containerId);
  }
}

package wx.service.sys.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import wx.dto.docker.ContainerStatus;
import wx.dto.docker.DockerPSArgument;
import wx.service.sys.DockerService;

/** @author wxyyxc1992 */
@Service
public class DockerServiceImpl implements DockerService {
  private DockerClient docker;

  public DockerServiceImpl(DockerClient docker) {
    this.docker = docker;
  }

  @Override
  public Info info() {
    return docker.infoCmd().exec();
  }

  @Override
  public List<Image> images() {
    return docker.listImagesCmd().exec();
  }

  @Override
  public List<Container> ps(DockerPSArgument argument) {
    final ListContainersCmd cmd = docker.listContainersCmd();
    if (argument.getLimit() != null) {
      cmd.withLimit(argument.getLimit());
    }
    if (argument.getShowSize() != null) {
      cmd.withShowSize(argument.getShowSize());
    }
    if (argument.getShowAll() != null) {
      cmd.withShowAll(argument.getShowAll());
    }
    if (argument.getSinceId() != null) {
      cmd.withSince(argument.getSinceId());
    }
    if (argument.getBeforeId() != null) {
      cmd.withBefore(argument.getBeforeId());
    }
    if (argument.getAncestorFilters() != null) {
      cmd.withAncestorFilter(argument.getAncestorFilters());
    }
    if (argument.getExitCodeFilter() != null) {
      cmd.withExitedFilter(argument.getExitCodeFilter());
    }
    if (argument.getContainerIdsFilter() != null) {
      cmd.withIdFilter(argument.getContainerIdsFilter());
    }
    if (argument.getLabelFilter() != null) {
      cmd.withLabelFilter(argument.getLabelFilter());
    }
    if (argument.getContainerNamesFilter() != null) {
      cmd.withNameFilter(argument.getContainerNamesFilter());
    }
    if (argument.getStatusesFilter() != null) {
      cmd.withStatusFilter(argument.getStatusesFilter());
    }
    if (argument.getNetworksFilter() != null) {
      cmd.withNetworkFilter(argument.getNetworksFilter());
    }
    if (argument.getVolumesFilter() != null) {
      cmd.withVolumeFilter(argument.getVolumesFilter());
    }

    return cmd.exec();
  }

  @Override
  public void status(String containerId, DeferredResult<ContainerStatus> deferredResult) {
    docker
        .statsCmd(containerId)
        .exec(
            new ResultCallback<Statistics>() {
              @Override
              public void onStart(Closeable closeable) {}

              @Override
              public void onNext(Statistics statistics) {

                if (statistics != null) {
                  ContainerStatus containerStatus = new ContainerStatus();
                  MemoryStatsConfig memoryStatsConfig = statistics.getMemoryStats();
                  if (memoryStatsConfig != null && memoryStatsConfig.getUsage() != null) {
                    containerStatus.setMemoryUsage(memoryStatsConfig.getUsage());
                  }

                  // 设置 CPU 状态
                  CpuStatsConfig cpuStatsConfig = statistics.getCpuStats();

                  if (cpuStatsConfig != null && cpuStatsConfig.getCpuUsage() != null) {
                    CpuUsageConfig cpuUsageConfig = cpuStatsConfig.getCpuUsage();

                    if (cpuUsageConfig != null && cpuUsageConfig.getTotalUsage() != null) {
                      containerStatus.setCpuUsage(cpuUsageConfig.getTotalUsage());
                    }
                  }

                  deferredResult.setResult(containerStatus);
                }
              }

              @Override
              public void onError(Throwable throwable) {}

              @Override
              public void onComplete() {}

              @Override
              public void close() throws IOException {
                // 这里判断 deferredResult 是否有返回，没有则设置
                if (!deferredResult.isSetOrExpired()) {
                  deferredResult.setResult(null);
                }
              }
            });
  }

  @Override
  public InspectContainerResponse inspect(String containerId) {
    return docker.inspectContainerCmd(containerId).exec();
  }
}

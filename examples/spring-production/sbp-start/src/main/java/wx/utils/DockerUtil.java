package wx.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 注意： 使用 {@code <do_something>ByName} 时，一个名字查出多个将抛出异常；比如存在容器 abc 和 abcd, 用 {@link
 * #findContainerByName(String)}方法查询 abc 将抛出异常，使用 {@link #removeContainerByName(String)} 也是一样，会抛出异常
 */
@Slf4j
public class DockerUtil {
  @Getter private DockerClient docker;

  public DockerUtil(DockerClient docker) {
    this.docker = docker;
  }

  public DockerUtil() {
    this.docker = DockerClientBuilder.getInstance().build();
  }

  public static HostConfig getOrCreateAndSetHostConfig(CreateContainerCmd cmd) {
    HostConfig hostConfig = cmd.getHostConfig();
    if (hostConfig == null) {
      hostConfig = new HostConfig();
      cmd.withHostConfig(hostConfig);
    }
    return hostConfig;
  }

  /** 映射端口 */
  public static CreateContainerCmd bindPort(
      CreateContainerCmd cmd, Integer hostPort, Integer containerPort, @Nullable String host) {
    HostConfig hostConfig = getOrCreateAndSetHostConfig(cmd);
    final List<ExposedPort> exposedPorts =
        cmd.getExposedPorts() == null ? new ArrayList<>() : Arrays.asList(cmd.getExposedPorts());
    final Ports ports =
        hostConfig.getPortBindings() == null ? new Ports() : hostConfig.getPortBindings();
    ExposedPort port = ExposedPort.tcp(containerPort);
    exposedPorts.add(port);
    ports.add(new PortBinding(new Binding(host, Integer.toString(hostPort)), port));
    hostConfig.withPortBindings(ports);
    return cmd.withExposedPorts(exposedPorts).withHostConfig(hostConfig);
  }

  /** 映射卷 */
  public static CreateContainerCmd bindVolume(
      CreateContainerCmd cmd, String volumeName, String containerPath) {
    HostConfig hostConfig = getOrCreateAndSetHostConfig(cmd);
    List<Volume> volumes =
        cmd.getVolumes() == null ? new ArrayList<>() : Arrays.asList(cmd.getVolumes());
    List<Bind> binds =
        hostConfig.getBinds() == null ? new ArrayList<>() : Arrays.asList(hostConfig.getBinds());

    final Volume volume = new Volume(volumeName);
    volumes.add(volume);
    binds.add(new Bind(containerPath, volume));
    return cmd.withVolumes(volumes)
        .withHostConfig(getOrCreateAndSetHostConfig(cmd).withBinds(binds));
  }

  public void saveImage(String name, File dstFile) {
    try {
      IOUtils.copy(
          docker.saveImageCmd(name).exec(),
          new BufferedOutputStream(new FileOutputStream(dstFile)));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void loadImage(File srcFile) {
    try {
      docker.loadImageCmd(new BufferedInputStream(new FileInputStream(srcFile)));
    } catch (FileNotFoundException e) {
      throw new UncheckedIOException(e);
    }
  }

  public CreateContainerCmd createCreateContainerCmd(
      String image, Consumer<CreateContainerCmd> cmdConfigurer) {
    CreateContainerCmd cmd = getDocker().createContainerCmd(image);
    cmdConfigurer.accept(cmd);
    return cmd;
  }

  /** 使用镜像名查找镜像 */
  public Optional<Image> findImageByName(String imageName) {
    final List<Image> images = docker.listImagesCmd().withImageNameFilter(imageName).exec();
    checkState(images.size() <= 1, "More than one image found with [name=%s]", imageName);
    return images.stream().findAny();
  }

  public List<Container> listContainersFilterByName(String name) {
    return docker.listContainersCmd().withNameFilter(Collections.singleton(name)).exec();
  }

  public List<Container> listContainers() {
    return docker.listContainersCmd().exec();
  }

  /** 使用容器名查找容器 */
  public Optional<Container> findContainerByName(String name) {
    requireNonNull(name, "name is null");
    final List<Container> containers =
        docker.listContainersCmd().withShowAll(true).withNameFilter(Collections.singletonList(name))
            .exec().stream()
            .filter(c -> ArrayUtils.contains(c.getNames(), name))
            .collect(Collectors.toList());
    checkState(containers.size() <= 1, "More than one containers found with [name=%s]", name);
    return containers.stream().findAny();
  }

  /** 使用容器名删除容器 */
  public void removeContainerByName(String name) {
    requireNonNull(name, "name is null");
    findContainerByName(name)
        .map(Container::getId)
        .map(docker::removeContainerCmd)
        .ifPresent(RemoveContainerCmd::exec);
  }

  /** 使用网络名查找容器 */
  public Optional<Network> findNetworkByName(String name) {
    requireNonNull(name, "name is null");
    final List<Network> networks =
        docker.listNetworksCmd().withNameFilter(name).exec().stream()
            .filter(n -> name.equals(n.getName()))
            .collect(Collectors.toList());
    checkState(networks.size() <= 1, "More than one networks found with [name=%s]", name);
    return networks.stream().findAny();
  }

  public Optional<InspectVolumeResponse> findVolumeByName(String name) {
    requireNonNull(name, "name is null");
    final List<InspectVolumeResponse> volumes =
        docker.listVolumesCmd().withFilter("name", Collections.singleton(name)).exec().getVolumes()
            .stream()
            .filter(v -> name.equals(v.getName()))
            .collect(Collectors.toList());
    checkState(volumes.size() <= 1, "more than one volumes found [name=%s]", name);
    return volumes.stream().findAny();
  }

  public InspectVolumeResponse createOrGetVolume(String name) {
    return findVolumeByName(name)
        .orElseGet(
            () ->
                findVolumeByName(docker.createVolumeCmd().withName(name).exec().getName())
                    .orElseThrow(RuntimeException::new));
  }

  /**
   * @param name 卷名
   * @param dataFile 卷备份文件，可以为空
   */
  public InspectVolumeResponse createVolume(String name, @Nullable File dataFile) {
    checkState(
        dataFile == null || dataFile.exists(),
        "volume [name=%s] data file %s not exists",
        name,
        dataFile);
    checkState(!findVolumeByName(name).isPresent(), "volume [name=%s] already exists", name);
    final InspectVolumeResponse response = createOrGetVolume(name);
    recoverVolume(name, dataFile);
    return response;
  }

  public void removeVolumeIfExists(String name) {
    findVolumeByName(name).ifPresent(res -> docker.removeVolumeCmd(name).exec());
  }

  public void backupVolume(String volumeName, File targetFile) throws IOException {
    checkArgument(!targetFile.exists(), "targetFile exists: %s", targetFile);
    checkState(findVolumeByName(volumeName).isPresent(), "volume[name=%s] not found", volumeName);
    FileUtils.forceMkdirParent(targetFile);
    final Volume srcVolume = new Volume("/backup/src");
    final Volume dstVolume = new Volume("/backup/dst");
    createAndStartAndWaitContainerAndRemove(
            "busybox",
            cmd ->
                cmd.withVolumes(srcVolume, dstVolume)
                    .withHostConfig(
                        new HostConfig()
                            .withBinds(
                                new Bind(volumeName, srcVolume),
                                new Bind(targetFile.getParent(), dstVolume, AccessMode.rw)))
                    .withWorkingDir("/backup/src")
                    .withCmd(
                        "tar",
                        "-czf",
                        new File("/backup/dst", targetFile.getName()).toString(),
                        "."))
        .join();
  }

  public void recoverVolume(String volumeName, File backupFile) {
    checkArgument(backupFile.exists(), "backupFile not exists: %s", backupFile);
    checkState(findVolumeByName(volumeName).isPresent(), "volume[name=%s] not found", volumeName);
    final Volume srcVolume = new Volume("/backup/src");
    final Volume dstVolume = new Volume("/backup/dst");
    createAndStartAndWaitContainerAndRemove(
            "busybox",
            cmd ->
                cmd.withVolumes(srcVolume, dstVolume)
                    .withHostConfig(
                        new HostConfig()
                            .withBinds(
                                new Bind(backupFile.getParent(), srcVolume, AccessMode.ro),
                                new Bind(volumeName, dstVolume, AccessMode.rw)))
                    .withWorkingDir("/backup/dst")
                    .withCmd(
                        "tar", "xfz", new File("/backup/src", backupFile.getName()).toString()))
        .join();
  }

  public CreateContainerResponse createAndStartContainer(CreateContainerCmd cmd) {
    CreateContainerResponse response;
    try {
      response = cmd.exec();
    } catch (Exception e) {
      log.warn(String.format("容器 %s 创建失败", cmd.getName()), e);
      throw e;
    }
    try {
      docker.startContainerCmd(response.getId()).exec();
      return response;
    } catch (Exception e) {
      log.warn(String.format("容器 %s 启动失败，将移除启动失败容器", cmd.getName()), e);
      docker.removeContainerCmd(response.getId()).exec();
      throw e;
    }
  }

  /** 启动并等待容器退出， exit code 不等于 0 抛出异常 */
  public CompletableFuture<Void> createAndStartAndWaitContainer(CreateContainerCmd cmd) {
    final CompletableFuture<Void> future = new CompletableFuture<>();
    final String id = createAndStartContainer(cmd).getId();
    Supplier<Void> removeByHand =
        () -> {
          if (cmd.getHostConfig() != null
              && cmd.getHostConfig().getAutoRemove() != null
              && cmd.getHostConfig().getAutoRemove()) {
            log.debug("autoremove {} by hand", id);
            docker.removeContainerCmd(id).exec();
          }
          return null;
        };
    docker
        .waitContainerCmd(id)
        .exec(
            new ResultCallback<WaitResponse>() {
              @Override
              public void onStart(Closeable closeable) {}

              @Override
              public void onNext(WaitResponse object) {
                removeByHand.get();
                if (object.getStatusCode() != 0) {
                  future.completeExceptionally(
                      new RuntimeException(
                          String.format("exited with %s", object.getStatusCode())));
                } else {
                  future.complete(null);
                }
              }

              @Override
              public void onError(Throwable throwable) {
                removeByHand.get();
                future.completeExceptionally(throwable);
              }

              @Override
              public void onComplete() {
                removeByHand.get();
                future.complete(null);
              }

              @Override
              public void close() throws IOException {}
            });
    return future;
  }

  public CompletableFuture<Void> createAndStartAndWaitContainerAndRemove(CreateContainerCmd cmd) {
    log.debug("toggle auto remove");
    if (cmd.getHostConfig() == null) {
      cmd.withHostConfig(new HostConfig().withAutoRemove(true));
    } else {
      cmd.getHostConfig().withAutoRemove(true);
    }
    return createAndStartAndWaitContainer(cmd);
  }

  public CreateContainerResponse createAndStartContainer(
      String imageName, Consumer<CreateContainerCmd> cmdConsumer) {
    final CreateContainerCmd cmd = docker.createContainerCmd(imageName);
    cmdConsumer.accept(cmd);
    return createAndStartContainer(cmd);
  }

  public CompletableFuture<Void> createAndStartAndWaitContainer(
      String imageName, Consumer<CreateContainerCmd> cmdConsumer) {
    final CreateContainerCmd cmd = docker.createContainerCmd(imageName);
    cmdConsumer.accept(cmd);
    return createAndStartAndWaitContainer(cmd);
  }

  public CompletableFuture<Void> createAndStartAndWaitContainerAndRemove(
      String imageName, Consumer<CreateContainerCmd> cmdConsumer) {
    final CreateContainerCmd cmd = docker.createContainerCmd(imageName);
    cmdConsumer.accept(cmd);
    return createAndStartAndWaitContainerAndRemove(cmd);
  }
}

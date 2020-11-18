package wx.domain.terminal.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public class ContainerConfig {
  @JsonProperty(required = true)
  String imageName;

  @JsonProperty(required = true)
  String containerName;

  List<VolumeConfig> volumes = new ArrayList<>();

  List<PortMapping> portMappings = new ArrayList<>();

  public ContainerConfig(
      String imageName,
      String containerName,
      List<VolumeConfig> volumes,
      List<PortMapping> portMappings) {
    this.imageName = imageName;
    this.containerName = containerName;
    this.volumes = volumes;
    this.portMappings = portMappings;
  }

  @Getter
  @NoArgsConstructor
  public static class VolumeConfig {
    @JsonProperty(required = true)
    String volumeName;

    @JsonProperty(required = true)
    String volumePath;

    @Nullable String baseDataFile;

    /**
     * @param volumeName 卷名
     * @param volumePath 卷在容器内部路径
     * @param baseDataFile 基础数据，初次创建卷时用于恢复的数据
     */
    public VolumeConfig(String volumeName, String volumePath, @Nullable String baseDataFile) {
      this.volumeName = volumeName;
      this.volumePath = volumePath;
      this.baseDataFile = baseDataFile;
    }
  }

  @Getter
  @NoArgsConstructor
  public static class PortMapping {
    String hostAddress;

    @JsonProperty(required = true)
    Integer hostPort;

    @JsonProperty(required = true)
    Integer containerPort;

    public PortMapping(Integer containerPort) {
      this(containerPort, containerPort);
    }

    public PortMapping(Integer hostPort, Integer containerPort) {
      this(hostPort, containerPort, null);
    }

    public PortMapping(Integer hostPort, Integer containerPort, @Nullable String hostAddress) {
      this.hostAddress = hostAddress == null ? "0.0.0.0" : hostAddress;
      this.hostPort = hostPort;
      this.containerPort = containerPort;
    }
  }
}

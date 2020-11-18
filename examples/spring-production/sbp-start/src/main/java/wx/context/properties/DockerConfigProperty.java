package wx.context.properties;

import lombok.Getter;

public class DockerConfigProperty {
  @Getter private String dockerHost;

  @Getter private String apiVersion;

  @Getter private String registryUsername;

  @Getter private String registryPassword;

  @Getter private String registryEmail;

  @Getter private String registryUrl;

  @Getter private String effectiveAuthConfig;
}

package wx.application.utils;

import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@Component
public class EnvironmentUtils {

  private Environment environment;

  public EnvironmentUtils(Environment environment) {
    this.environment = environment;
  }

  public boolean isTestOrDevEnv() {
    for (String activeProfile : environment.getActiveProfiles()) {
      if ("test".equals(activeProfile) || "dev".equals(activeProfile)) {
        return true;
      }
    }
    return false;
  }
}

package wx.domain.terminal.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfig {
  ProductConfig product;
  CendertronConfig cendertron;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ProductConfig {
    String host;
    Integer port;
    String vulns;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CendertronConfig {
    String host;
    Integer port;
  }
}

package wx.domain.update;

import lombok.Data;

@Data
public class WsatUpdateManifest {
  // agent jar
  String agentJar;

  // wsat-product tar.gz
  String productImage;

  // wsat-cscan tar.gz
  String cscanImage;

  // poc package tar.gz
  String pocPackage;
}

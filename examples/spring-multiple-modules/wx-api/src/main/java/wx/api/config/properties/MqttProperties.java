package wx.api.config.properties;

import lombok.Data;

/**
 * MqttProperties.
 *
 * @author lotuc
 */
@Data
public class MqttProperties {
  private String username;
  private String password;
  private int maxBytesInMessage;
  private boolean reconnect;
  private String brokerHost;
  private int brokerPort;
}

package wx.infra.service.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailSetting {

  private String host;

  private String username;

  private String password;

  private Integer port;

  private String personal;
}

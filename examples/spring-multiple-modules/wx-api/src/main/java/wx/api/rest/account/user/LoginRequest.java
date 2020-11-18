package wx.api.rest.account.user;

import lombok.Data;

@Data
public class LoginRequest {

  String username;

  CredentialType credentialType;

  String credential;
}

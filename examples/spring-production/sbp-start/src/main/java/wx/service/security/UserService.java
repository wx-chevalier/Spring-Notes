package wx.service.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import wx.context.security.JWTPrincipal;

public interface UserService extends UserDetailsService {
  JWTPrincipal login(String username, String password);
}

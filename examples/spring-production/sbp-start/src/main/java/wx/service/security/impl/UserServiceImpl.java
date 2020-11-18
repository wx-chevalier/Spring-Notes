package wx.service.security.impl;

import com.google.common.collect.Sets;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wx.context.security.JWTPrincipal;
import wx.constants.UserRole;
import wx.domain.terminal.config.AdminInterfaceConfig;
import wx.domain.terminal.config.TerminalConfig;
import wx.service.security.UserService;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private TerminalConfig terminalConfig;

  public UserServiceImpl(
      BCryptPasswordEncoder bCryptPasswordEncoder, TerminalConfig terminalConfig) {
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.terminalConfig = terminalConfig;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JWTPrincipal login(String username, String password) {
    log.info("Login attempt for User[name={}]", username);
    final AdminInterfaceConfig.User user = terminalConfig.getAdmin().getUser();

    System.out.println(user.getUsername() + user.getPassword());

    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
      return new JWTPrincipal(
          user.getUsername(), user.getUsername(), Sets.newHashSet(UserRole.ADMIN), new HashSet<>());
    }
    throw new AuthenticationException("登陆验证失败") {};
  }
}

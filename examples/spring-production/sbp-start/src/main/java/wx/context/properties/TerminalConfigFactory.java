package wx.context.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import wx.domain.terminal.config.TerminalConfig;

@Component
@Slf4j
public class TerminalConfigFactory {
  private ApplicationProperty applicationProperty;

  public TerminalConfigFactory(ApplicationProperty applicationProperty) {
    this.applicationProperty = applicationProperty;
  }

  @Bean
  public TerminalConfig terminalConfig() {
    return TerminalConfig.parseConfig();
  }
}

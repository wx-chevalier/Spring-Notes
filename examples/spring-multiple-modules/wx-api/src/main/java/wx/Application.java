package wx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.Formatter;
import org.springframework.scheduling.annotation.EnableScheduling;
import wx.api.config.properties.ApplicationProperties;
import wx.api.config.security.JwtTokenConfig;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = {Application.class})
public class Application {
  private ApplicationProperties applicationProperties;

  public Application(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public JwtTokenConfig jwtTokenConfig() {
    return applicationProperties.getSecurity().getJwt();
  }

  @Bean
  public Formatter<LocalDate> localDateFormatter() {
    return new Formatter<LocalDate>() {
      @NotNull
      @Override
      public LocalDate parse(@NotNull String text, @NotNull Locale locale) {
        return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
      }

      @NotNull
      @Override
      public String print(@NotNull LocalDate object, @NotNull Locale locale) {
        return DateTimeFormatter.ISO_DATE.format(object);
      }
    };
  }
}

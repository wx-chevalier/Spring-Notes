package wx.api.config.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import wx.api.config.properties.ApplicationProperties;

@Configuration
public class CORSConfig {

  private final ApplicationProperties applicationProperties;

  public CORSConfig(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = applicationProperties.getCors();
    List<String> allowedOrigins = config.getAllowedOrigins();
    if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
      source.registerCorsConfiguration("/**", config);
      source.registerCorsConfiguration("/v2/api-docs", config);
    }
    return new CorsFilter(source);
  }
}

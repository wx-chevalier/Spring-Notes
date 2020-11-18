package wx.context.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import wx.context.properties.ApplicationProperty;
import wx.context.properties.SecurityProperty;
import wx.service.security.impl.UserServiceImpl;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private UserServiceImpl userDetailsService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private SecurityProperty securityProperty;
  private ObjectMapper objectMapper;

  public WebSecurity(
      UserServiceImpl userDetailsService,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      ApplicationProperty applicationProperty,
      ObjectMapper objectMapper) {
    this.userDetailsService = userDetailsService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.securityProperty = applicationProperty.getSecurity();
    this.objectMapper = objectMapper;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
        registry = http.cors().and().csrf().disable().authorizeRequests();

    // User permitted public accessible URLs
    securityProperty
        .getPublicUrls()
        .forEach(
            (method, urls) -> {
              if (urls.size() != 0) {
                registry.antMatchers(method, urls.toArray(new String[0]));
              }
            });

    registry
        .antMatchers("/actuator/**")
        .hasRole("ADMIN")
        // permit swagger ui resources
        .antMatchers(
            HttpMethod.GET,
            "/v2/api-docs",
            "/v2/domain-docs",
            "/swagger-resources/**",
            "/swagger-ui.html**",
            "/webjars/**",
            "favicon.ico")
        .permitAll()
        .anyRequest()
        // No auth for now
        .permitAll()
        // .authenticated()
        .and()
        // JWT Authorization
        .addFilter(
            new JWTAuthorizationFilter(
                authenticationManager(), this.securityProperty.getJwt(), this.objectMapper))
        // this disables session creation on Spring Security
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    if (this.securityProperty.getCors() != null) {
      source.registerCorsConfiguration("/**", this.securityProperty.getCors());
    }
    return source;
  }
}

package wx.api.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import wx.api.config.security.model.JwtTokenFactory;
import wx.domain.auth.AccessKeyRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private ObjectMapper objectMapper;
  private JwtTokenFactory jwtTokenFactory;
  private AccessKeyRepository accessKeyRepository;

  public WebSecurity(
      ObjectMapper objectMapper,
      JwtTokenFactory jwtTokenFactory,
      AccessKeyRepository accessKeyRepository) {
    this.objectMapper = objectMapper;
    this.jwtTokenFactory = jwtTokenFactory;
    this.accessKeyRepository = accessKeyRepository;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
        registry = http.cors().and().csrf().disable().authorizeRequests();
    // swagger
    registry
        .antMatchers(
            HttpMethod.GET,
            "/v2/api-docs",
            "/swagger-resources/**",
            "/doc.html",
            "/swagger-ui.html**",
            "/webjars/**",
            "favicon.ico")
        .permitAll();

    registry.antMatchers("/noauth/**").permitAll();
    registry.antMatchers("/wechat/**").permitAll();

    // 回调相关接口
    registry.antMatchers(HttpMethod.POST, "/callback/**").permitAll();

    registry
        // 登陆接口
        .antMatchers(HttpMethod.POST, "/user/login", "/user/login/validate")
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        // JWT 登陆验证
        .addFilter(
            new JwtAuthorizationFilter(
                authenticationManager(), jwtTokenFactory, accessKeyRepository))
        // this disables session creation on Spring Security
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomAuthenticationEntryPoint(objectMapper);
  }
}

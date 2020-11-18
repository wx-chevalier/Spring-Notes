package wx.context.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

@Data
public class SecurityProperty {
  private JwtConfig jwt = new JwtConfig();

  private Map<HttpMethod, List<String>> publicUrls = new HashMap<>();

  private CorsConfiguration cors = new CorsConfiguration();

  @Data
  public static class JwtConfig {
    private String secret = UUID.randomUUID().toString();
    private String authorityClaim;
    private Long expirationMs;

    private String tokenHeader;
    private String tokenPrefix = "Bearer ";
  }
}

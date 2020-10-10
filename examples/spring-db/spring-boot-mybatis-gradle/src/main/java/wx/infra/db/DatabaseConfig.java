package wx.infra.db;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackageClasses = {DatabaseConfig.class})
public class DatabaseConfig {

  @Bean
  public MybatisPlusPropertiesCustomizer customizer() {
    // columnFormat 默认对主键无效，可以在主键注解 @TableField(keepGlobalFormat = true) 使其生效
    return properties -> properties.getGlobalConfig().getDbConfig().setColumnFormat("`%s`");
  }
}

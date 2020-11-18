package wx.infra.tunnel.db;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "wx.infra.tunnel.db")
public class DatabaseConfig {

  /** 分页插件 */
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    paginationInterceptor.setLimit(100);
    return paginationInterceptor;
  }

  @Bean
  public MybatisPlusPropertiesCustomizer customizer() {
    // columnFormat 默认对主键无效，可以在主键注解 @TableField(keepGlobalFormat = true) 使其生效
    return properties -> properties.getGlobalConfig().getDbConfig().setColumnFormat("`%s`");
  }
}

package wx.repository.wsat.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import wx.domain.terminal.config.DbConfig;
import wx.domain.terminal.config.TerminalConfig;

@Configuration
@MapperScan(
    basePackageClasses = {wx.repository.wsat.dao.WsatDbConfig.class},
    sqlSessionFactoryRef = "wsatMySQLSessionFactory")
@Slf4j
public class WsatDbConfig {
  private TerminalConfig terminalConfig;

  public WsatDbConfig(TerminalConfig terminalConfig) {
    this.terminalConfig = terminalConfig;
  }

  @Bean("wsatMySQLDataSource")
  public DataSource wsatMySQLDataSource() {
    final DbConfig.MySQLConfig wsatMySQL = terminalConfig.getDb().getMysql();
    final DataSourceBuilder<?> builder =
        DataSourceBuilder.create()
            .username(wsatMySQL.getUser())
            .driverClassName("com.mysql.jdbc.Driver")
            .password(wsatMySQL.getPassword());

    final String containerUrl =
        String.format(
            "jdbc:mysql://%s/%s",
            wsatMySQL.getContainer().getContainerName(), wsatMySQL.getDatabase());
    final String hostUrl =
        String.format(
            "jdbc:mysql://%s:%d/%s",
            wsatMySQL.getHost(), wsatMySQL.getPort(), wsatMySQL.getDatabase());

    final DataSource dsContainer = builder.url(containerUrl).build();
    try (Connection connection = dsContainer.getConnection()) {
      log.info("使用容器网络连接数据库 {}", containerUrl);
      return dsContainer;
    } catch (SQLException e) {
      log.warn("使用容器网络连接失败");
    }
    final DataSource dsHost = builder.url(hostUrl).build();
    try (Connection connection = dsHost.getConnection()) {
      log.info("使用主机网络连接数据库 {}", hostUrl);
      return dsHost;
    } catch (SQLException e) {
      log.warn("使用主机网络连接失败");
    }

    log.info("默认使用容器网络连接，等待数据库恢复");
    return dsContainer;
  }

  @Bean("wsatMySQLSessionFactory")
  public SqlSessionFactoryBean wsatMySQLSessionFactory() throws IOException {
    final SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(wsatMySQLDataSource());
    bean.setMapperLocations(
        new PathMatchingResourcePatternResolver()
            .getResources("classpath*:com/cy/wsat/agent/repository/wsat/**/*.xml"));
    return bean;
  }
}

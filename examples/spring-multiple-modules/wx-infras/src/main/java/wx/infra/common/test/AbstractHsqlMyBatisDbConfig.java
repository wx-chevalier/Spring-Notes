package wx.infra.common.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public abstract class AbstractHsqlMyBatisDbConfig {

  protected abstract void configDataSourceBuilder(EmbeddedDatabaseBuilder builder);

  @Bean
  public DataSource dataSource() {
    EmbeddedDatabaseBuilder builder =
        new EmbeddedDatabaseBuilder()
            .setName(getClass().getTypeName() + Math.random())
            .setType(EmbeddedDatabaseType.HSQL);
    configDataSourceBuilder(builder);
    return builder.build();
  }

  @Bean
  public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
  }

  @Bean
  public GlobalConfig globalConfig() {
    return new GlobalConfig()
        .setEnableSqlRunner(true)
        .setDbConfig(
            new GlobalConfig.DbConfig()
                .setLogicDeleteValue("1")
                .setLogicNotDeleteValue("0")
                .setKeyGenerator(new H2KeyGenerator())
                .setIdType(IdType.AUTO));
  }

  @Bean("mybatisSqlSession")
  public SqlSessionFactory sqlSessionFactory(
      DataSource dataSource, GlobalConfig globalConfig, PaginationInterceptor paginationInterceptor)
      throws Exception {
    return MyBatisTestUtils.createMyBatisSqlSessionFactory(
        dataSource, globalConfig, paginationInterceptor);
  }
}

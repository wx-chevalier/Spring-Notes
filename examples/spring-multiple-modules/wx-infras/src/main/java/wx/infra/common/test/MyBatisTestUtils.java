package wx.infra.common.test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class MyBatisTestUtils {

  public static SqlSessionFactory createMyBatisSqlSessionFactory(
      DataSource dataSource, @Nullable GlobalConfig globalConfig, Interceptor... plugins)
      throws Exception {
    MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
    sqlSessionFactory.setDataSource(dataSource);

    // mybatis configuration
    MybatisConfiguration configuration = new MybatisConfiguration();
    configuration.setJdbcTypeForNull(JdbcType.NULL);
    configuration.setMapUnderscoreToCamelCase(true);
    configuration.setDefaultExecutorType(ExecutorType.REUSE);
    // Default enum handler
    configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
    sqlSessionFactory.setConfiguration(configuration);

    // plugins & mybatis plus global configuration
    if (globalConfig != null) {
      sqlSessionFactory.setGlobalConfig(globalConfig);
    }
    sqlSessionFactory.setPlugins(plugins);

    return sqlSessionFactory.getObject();
  }
}

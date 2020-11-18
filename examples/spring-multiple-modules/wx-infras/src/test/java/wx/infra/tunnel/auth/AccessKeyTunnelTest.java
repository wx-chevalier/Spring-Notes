package wx.infra.tunnel.db.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import wx.infra.common.test.AbstractHsqlMyBatisDbConfig;
import wx.infra.tunnel.db.mapper.auth.AccessKeyMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AccessKeyTunnelTest.TestConfig.class, AccessKeyTunnel.class})
class AccessKeyTunnelTest {

  @Autowired private AccessKeyTunnel tunnel;

  @Test
  void testList() {
    assertEquals(0, tunnel.list().size());
  }

  @Configuration
  @MapperScan(basePackageClasses = {AccessKeyMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder
          .setName("auth_access_key")
          .addScript("classpath:db/schema/hsql/create_auth_schema.sql");
    }
  }
}

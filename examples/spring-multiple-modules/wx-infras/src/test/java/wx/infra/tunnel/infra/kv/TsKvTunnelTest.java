package wx.infra.tunnel.db.infra.kv;

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
import wx.infra.tunnel.db.mapper.infra.kv.TsKvMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TsKvTunnelTest.TestConfig.class, TsKvTunnel.class})
class TsKvTunnelTest {

  @Autowired private TsKvTunnel tsKvTunnel;

  @Test
  void testList() {
    assertEquals(0, tsKvTunnel.list().size());
  }

  @Configuration
  @MapperScan(basePackageClasses = {TsKvMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder.setName("infra_kv").addScript("classpath:db/schema/hsql/create_infra_schema.sql");
    }
  }
}

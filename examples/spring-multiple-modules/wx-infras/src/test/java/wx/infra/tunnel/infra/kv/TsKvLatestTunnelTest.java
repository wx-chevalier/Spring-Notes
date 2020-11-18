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
import wx.infra.tunnel.db.mapper.infra.kv.TsKvLatestMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TsKvLatestTunnelTest.TestConfig.class, TsKvLatestTunnel.class})
public class TsKvLatestTunnelTest {

  @Autowired private TsKvLatestTunnel tsKvLatestTunnel;

  @Test
  void testList() {
    assertEquals(0, tsKvLatestTunnel.list().size());
  }

  @Configuration
  @MapperScan(basePackageClasses = {TsKvLatestMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder
          .setName("infra_ts_kv_latest")
          .addScript("classpath:db/schema/hsql/create_infra_schema.sql");
    }
  }
}

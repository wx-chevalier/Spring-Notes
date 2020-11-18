package wx.infra.tunnel.db.admin;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
import wx.infra.tunnel.db.mapper.admin.PermissionSettingMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {PermissionSettingTunnelTest.TestConfig.class, PermissionSettingTunnel.class})
@Configuration
class PermissionSettingTunnelTest {

  @Autowired private PermissionSettingTunnel permissionSettingTunnel;

  @Test
  void testList() {
    assertNotEquals(0, permissionSettingTunnel.list().size());
  }

  @Configuration
  @MapperScan(basePackageClasses = {PermissionSettingMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder
          .setName("admin_permission_setting")
          .addScript("classpath:db/schema/hsql/create_admin_schema.sql")
          .addScript("classpath:db/data/common/create_admin_system_data.sql")
          .addScript("classpath:db/data/common/create_admin_test_data.sql");
    }
  }
}

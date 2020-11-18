package wx.infra.tunnel.db.infra.file;

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
import wx.infra.tunnel.db.mapper.infra.file.FileAttrMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FileAttrTunnelTest.TestConfig.class, FileAttrTunnel.class})
class FileAttrTunnelTest {

  @Autowired private FileAttrTunnel fileAttrTunnel;

  @Test
  void testList() {
    assertEquals(0, fileAttrTunnel.list().size());
  }

  @Configuration
  @MapperScan(basePackageClasses = {FileAttrMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder
          .setName("infra_file_attr")
          .addScript("classpath:db/schema/hsql/create_infra_schema.sql");
    }
  }
}

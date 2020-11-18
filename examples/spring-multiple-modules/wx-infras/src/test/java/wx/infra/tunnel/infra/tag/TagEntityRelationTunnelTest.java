package wx.infra.tunnel.db.infra.tag;

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
import wx.infra.tunnel.db.mapper.infra.tag.TagEntityRelationMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {TagEntityRelationTunnelTest.TestConfig.class, TagEntityRelationTunnel.class})
class TagEntityRelationTunnelTest {

  @Autowired private TagEntityRelationTunnel tagEntityRelationTunnel;

  @Test
  void testList() {
    assertEquals(0, tagEntityRelationTunnel.list().size());
  }

  @Configuration
  @MapperScan(basePackageClasses = {TagEntityRelationMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder
          .setName("infra_tag_entity_relation")
          .addScript("classpath:db/schema/hsql/create_infra_schema.sql");
    }
  }
}

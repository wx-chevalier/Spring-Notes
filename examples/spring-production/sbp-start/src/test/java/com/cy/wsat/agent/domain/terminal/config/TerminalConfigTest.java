package wx.domain.terminal.config;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class TerminalConfigTest {

  @Test
  void testReadConfig() {
    final TerminalConfig config = TerminalConfig.parseConfig();

    // AdminConfig
    assertEquals("admin", config.getAdmin().getUser().getUsername());
    assertEquals("AdminConfig1314", config.getAdmin().getUser().getPassword());

    // DbConfig
    assertEquals("mysql.wsat", config.getDb().getMysql().getHost());
    assertEquals(new Integer(3306), config.getDb().getMysql().getPort());
    assertEquals("root", config.getDb().getMysql().getUser());
    assertEquals("wsat.mysql.pass", config.getDb().getMysql().getPassword());
    assertEquals("wsat", config.getDb().getMysql().getDatabase());

    assertEquals("redis.wsat", config.getDb().getRedis().getHost());
    assertEquals(new Integer(6387), config.getDb().getRedis().getPort());
    assertEquals("wsat.redis.pass", config.getDb().getRedis().getPassword());

    // ServiceConfig
    assertEquals("0.0.0.0", config.getService().getProduct().getHost());
    assertEquals(new Integer(8080), config.getService().getProduct().getPort());
    assertEquals("cendertron.wsat", config.getService().getCendertron().getHost());
    assertEquals(new Integer(5000), config.getService().getCendertron().getPort());
  }
}

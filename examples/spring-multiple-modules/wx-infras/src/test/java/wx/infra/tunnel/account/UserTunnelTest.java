package wx.infra.tunnel.db.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import wx.common.data.shared.id.*;
import wx.infra.common.test.AbstractHsqlMyBatisDbConfig;
import wx.infra.tunnel.db.mapper.account.UserMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserTunnelTest.TestConfig.class, UserTunnel.class})
class UserTunnelTest {

  @Autowired private UserTunnel userTunnel;

  @Test
  void testSave() {
    String username = "lotuc";
    String phoneNumber = "15012345678";
    String email = "lotuc@lotuc.org";
    UserDO userDO =
        new UserDO()
            .setTenantId(1L)
            .setUsername(username)
            .setPhoneNumber(phoneNumber)
            .setNickName("lotuc")
            .setEmail(email)
            .setCreatorId(1L);
    userTunnel.save(userDO);
    assertNotNull(userDO.getId());

    UserDO findById = userTunnel.getById(userDO.getId());
    assertNotNull(findById);
    assertEquals(userDO, findById);
  }

  @ParameterizedTest
  @MethodSource("testData")
  void testFindByUsername(UserDO testUser) {
    TenantId tenantId = new TenantId(testUser.getTenantId());
    UserDO findById = userTunnel.getById(testUser.getId());
    assertNotNull(findById);

    Optional<UserDO> findByUsername = userTunnel.findByUsername(tenantId, testUser.getUsername());
    assertTrue(findByUsername.isPresent());
    assertEquals(findById, findByUsername.get());

    Optional<UserDO> findByPhoneNumber =
        userTunnel.findByUsername(tenantId, testUser.getPhoneNumber());
    assertTrue(findByPhoneNumber.isPresent());
    assertEquals(findById, findByPhoneNumber.get());

    Optional<UserDO> findByEmail = userTunnel.findByUsername(tenantId, testUser.getEmail());
    assertTrue(findByEmail.isPresent());
    assertEquals(findById, findByEmail.get());
  }

  private static Stream<UserDO> testData() {
    return Stream.of(
        new UserDO()
            .setId(10L)
            .setTenantId(0L)
            .setUsername("ua-admin")
            .setPhoneNumber("42424242420")
            .setNickName("ua-admin")
            .setEmail("ua-admin@unionfab.com")
            .setCreatorId(0L),
        new UserDO()
            .setId(11L)
            .setTenantId(0L)
            .setUsername("ua-test1")
            .setPhoneNumber("42424242421")
            .setNickName("ua-test1")
            .setEmail("ua-test1@unionfab.com")
            .setCreatorId(0L));
  }

  @Configuration
  @MapperScan(basePackageClasses = {UserMapper.class})
  @EnableTransactionManagement
  public static class TestConfig extends AbstractHsqlMyBatisDbConfig {

    @Override
    protected void configDataSourceBuilder(EmbeddedDatabaseBuilder builder) {
      builder
          .setName("u_user")
          .addScript("classpath:db/schema/hsql/create_account_schema.sql")
          .addScript("classpath:db/data/common/create_account_system_data.sql")
          .addScript("classpath:db/data/common/create_account_test_data.sql");
    }
  }
}

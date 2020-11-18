package wx;

import com.cy.wsat.config.UnitTestProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import wx.Application;
import wx.context.properties.ApplicationProperty;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@UnitTestProfile
public class TestApplication {
  @Autowired private ApplicationProperty applicationProperty;

  @Test
  public void contextLoads() {}

  @Test
  public void testApplicationPropertiesLoads() {
    assertEquals("unit-test-secret", applicationProperty.getSecurity().getJwt().getSecret());
    assertTrue(applicationProperty.getSecurity().getPublicUrls().containsKey(HttpMethod.POST));
    assertArrayEquals(
        new String[] {"/user/sign-up", "/hello"},
        applicationProperty.getSecurity().getPublicUrls().get(HttpMethod.POST).toArray());
  }
}

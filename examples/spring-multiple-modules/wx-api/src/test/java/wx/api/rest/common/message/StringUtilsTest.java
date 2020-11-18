package wx.api.rest.common.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wx.infra.common.util.StringUtil;

@Slf4j
public class StringUtilsTest {

  @Test
  public void testReplaceParam() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "姓名");
    map.put("link", "链接");
    map.put("nameLink", "姓名链接");
    String s = StringUtil.replaceAll("${name}  + ${link}  =  ${nameLink}", map);
    assertEquals("姓名  + 链接  =  姓名链接", s);
  }
}

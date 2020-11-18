package wx.infra.service.dingtalk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DingTalkServiceImplTest {

  private DingTalkServiceImpl dingTalkService;

  @BeforeEach
  void setUp() {
    dingTalkService =
        new DingTalkServiceImpl(
            new DingTalkSetting(
                "d957cf1687bd0383b003ea2c8f472d8b08a763afef95c1cd0402d8ea09f0093d"));
  }

  @Test
  @Disabled
  void testSendText() {
    dingTalkService.sendText("wx: hello", "15062227589");
  }

  @Test
  @Disabled
  void testSendMarkdown() {
    dingTalkService.sendMarkdown("wx: hello", "# python\n```python\nprint('hello')\n```");
  }

  @Test
  @Disabled
  void sendLink() {
    dingTalkService.sendLink("wx: link", "https://print.unionfab.com", "hello", null);
  }
}

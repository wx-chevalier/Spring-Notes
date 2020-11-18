package wx.api.rest.common.message;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wx.Application;
import wx.application.infra.notice.email.EmailService;
import wx.application.infra.notice.sms.SmsService;
import wx.application.infra.notice.wechat.WechatMessageService;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.UserId;

@Slf4j
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "local")
public class NoticeMessageTest {

  @Autowired private EmailService emailService;

  @Autowired private SmsService smsService;

  @Autowired private WechatMessageService wechatMessageService;

  @Test
  @Ignore("Demonstration")
  public void testSendEmail() throws InterruptedException {
    HashMap<String, String> paramMap = new HashMap<>();
    paramMap.put("nickName", "NICKNAME_EXAMPLE");

    emailService.send("zhoutao@unionteach3d.cn", NoticeType.DEVICE_ERROR, paramMap);
    emailService.sentBindNotice(new UserId(12L), "zhoutao@uniontech3d.cn", "");
    TimeUnit.SECONDS.sleep(60);
  }

  @Test
  @Ignore("Demonstration")
  public void testSendSMS() throws InterruptedException {
    HashMap<String, String> paramMap = new HashMap<>();
    smsService.send("zhoutao@unionteach3d.cn", NoticeType.BIND_PHONE, paramMap);
    TimeUnit.SECONDS.sleep(60);
  }
}

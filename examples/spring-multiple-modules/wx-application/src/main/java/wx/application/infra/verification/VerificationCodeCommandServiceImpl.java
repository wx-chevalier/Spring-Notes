package wx.application.infra.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.domain.infra.verificationcode.VerificationCode;
import wx.domain.infra.verificationcode.VerificationCodeCommandService;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.infra.verificationcode.VerificationCodeDO;
import wx.infra.tunnel.db.infra.verificationcode.VerificationCodeTunnel;

@Slf4j
@Service
public class VerificationCodeCommandServiceImpl implements VerificationCodeCommandService {

  private VerificationCodeTunnel verificationCodeTunnel;

  private VerificationCodeConverter verificationCodeConverter;

  public VerificationCodeCommandServiceImpl(
      VerificationCodeTunnel verificationCodeTunnel,
      VerificationCodeConverter verificationCodeConverter) {
    this.verificationCodeTunnel = verificationCodeTunnel;
    this.verificationCodeConverter = verificationCodeConverter;
  }

  @Override
  public VerificationCode getByCode(NoticeType noticeType, String code) {
    log.info("校验验证码: noticeType = {} code = {}", noticeType, code);
    VerificationCodeDO verificationCodeDO = verificationCodeTunnel.get(10 * 60, noticeType, code);
    if (verificationCodeDO == null) {
      log.info("校验验证码失败,验证码:{} 不存在或已失效", code);
      throw new NotFoundException("绑定失败,验证码不存在或已失效");
    }
    return verificationCodeConverter.convertFrom(verificationCodeDO);
  }

  @Override
  public boolean verifyCode(
      String sendDst, String code, NoticeSendChannel channel, NoticeType noticeType) {
    int count =
        verificationCodeTunnel.checkVerificationCode(10 * 60, code, noticeType, channel, sendDst);
    return count != 0;
  }

  @Override
  public int getSendCount(
      int second, String sendDst, NoticeType noticeType, NoticeSendChannel channel) {
    return verificationCodeTunnel.getSendCount(second, noticeType, channel, sendDst);
  }
}

package wx.domain.infra.verificationcode;

import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;

public interface VerificationCodeCommandService {

  /** 通过验证码Code获取当前验证码的信息 */
  VerificationCode getByCode(NoticeType noticeType, String code);

  /** 检查指定发送目标的发送个数 */
  int getSendCount(int sendCount, String sendDst, NoticeType noticeType, NoticeSendChannel channel);

  /** 校验验证码 */
  boolean verifyCode(String sendDst, String code, NoticeSendChannel channel, NoticeType noticeType);
}

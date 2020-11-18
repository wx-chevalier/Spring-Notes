package wx.api.rest.common.verificationcode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.infra.common.exception.DataValidationException;

@RestController
@Api(tags = "🔐 验证码相关接口")
public class VerificationCodeController extends BaseController {

  @PostMapping("/noauth/verification_code")
  @ApiOperation(value = "发送登陆登陆验证码请求")
  public ResponseEnvelope<Void> requestVerificationCode(
      @Valid @RequestBody VerificationCodeRequest request) {
    if (request.getChannel() == NoticeSendChannel.SMS) {
      smsService.sendLoginCode(request.getSendDst());
    } else {
      throw new DataValidationException("操作失败，暂不支持的验证码渠道");
    }
    return ResponseEnvelope.createOk();
  }

  @PostMapping("/bind_phone/verification_code")
  @ApiOperation(value = "发送绑定手机号的验证码")
  public ResponseEnvelope<Void> sendBoundPhoneVerificationCode(
      @Valid @RequestBody VerificationCodeRequest request) {
    smsService.sentBindNotice(getCurrentUserId(), request.getSendDst(), request.getPassword());
    return ResponseEnvelope.createOk();
  }

  @PostMapping("/bind_email/verification_code")
  @ApiOperation(value = "发送绑定邮箱的电子邮件")
  public ResponseEnvelope<Void> sendBoundEmailVerificationCode(
      @Valid @RequestBody VerificationCodeRequest request) {
    emailService.sentBindNotice(getCurrentUserId(), request.getSendDst(), request.getPassword());
    return ResponseEnvelope.createOk();
  }

  @PostMapping("/noauth/bind_email")
  @ApiOperation(value = "验证绑定邮箱的链接")
  public ResponseEnvelope<Void> verificationEmailLink(String code) {
    userCommandService.bindEmailAddress(code);
    return ResponseEnvelope.createOk();
  }
}

package wx.application.infra.verification;

import java.util.Optional;
import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.common.data.shared.id.VerificationCodeId;
import wx.domain.infra.verificationcode.VerificationCode;
import wx.infra.tunnel.db.infra.verificationcode.VerificationCodeDO;

@Component
public class VerificationCodeConverter
    extends AbstractConverter<VerificationCode, VerificationCodeDO> {

  @Override
  public VerificationCode convertFrom(VerificationCodeDO verificationCodeDO) {
    return new VerificationCode(
        new VerificationCodeId(verificationCodeDO.getId()),
        Optional.ofNullable(verificationCodeDO.getTenantId()).map(TenantId::new).orElse(null),
        verificationCodeDO.getCreatedAt(),
        verificationCodeDO.getUpdatedAt(),
        Optional.ofNullable(verificationCodeDO.getUserId()).map(UserId::new).orElse(null),
        verificationCodeDO.getCode(),
        verificationCodeDO.getType(),
        verificationCodeDO.getChannel(),
        verificationCodeDO.getSendDst(),
        verificationCodeDO.getVerifiedAt(),
        verificationCodeDO.getSentAt(),
        verificationCodeDO.getExpireAt());
  }

  @Override
  public VerificationCodeDO convertTo(VerificationCode verificationCode) {
    return new VerificationCodeDO()
        .setId(
            Optional.ofNullable(verificationCode.getId())
                .map(VerificationCodeId::getId)
                .orElse(null))
        .setTenantId(
            Optional.ofNullable(verificationCode.getTenantId()).map(TenantId::getId).orElse(null))
        .setUserId(
            Optional.ofNullable(verificationCode.getUserId()).map(UserId::getId).orElse(null))
        .setCode(verificationCode.getCode())
        .setType(verificationCode.getType())
        .setChannel(verificationCode.getSendChannel())
        .setSendDst(verificationCode.getSendDst())
        .setVerifiedAt(verificationCode.getVerifiedAt())
        .setSentAt(verificationCode.getSentAt())
        .setExpireAt(verificationCode.getExpireAt());
  }
}

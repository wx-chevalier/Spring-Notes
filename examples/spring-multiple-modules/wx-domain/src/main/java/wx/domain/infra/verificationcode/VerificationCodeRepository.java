package wx.domain.infra.verificationcode;

import java.util.Optional;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.VerificationCodeId;
import wx.domain.shared.IdBasedEntityRepository;

public interface VerificationCodeRepository
    extends IdBasedEntityRepository<VerificationCodeId, VerificationCode> {

  Optional<VerificationCode> findValidCodeByPhoneNumber(String phoneNumber, String code);

  Optional<VerificationCode> findValidCodeByEmail(String email, String code);

  Optional<VerificationCode> findValidCode(String sendDst, String code, NoticeType type);
}

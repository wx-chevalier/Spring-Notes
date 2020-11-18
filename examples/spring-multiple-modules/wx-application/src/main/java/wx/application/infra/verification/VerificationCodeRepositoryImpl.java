package wx.application.infra.verification;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.VerificationCodeId;
import wx.domain.infra.verificationcode.VerificationCode;
import wx.domain.infra.verificationcode.VerificationCodeRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.infra.verificationcode.VerificationCodeDO;
import wx.infra.tunnel.db.infra.verificationcode.VerificationCodeTunnel;
import wx.infra.tunnel.db.mapper.infra.validatecode.ValidateCodeMapper;

@Repository
public class VerificationCodeRepositoryImpl
    extends MyBatisIdBasedEntityRepository<
        VerificationCodeTunnel,
        ValidateCodeMapper,
        VerificationCodeDO,
        VerificationCode,
        VerificationCodeId>
    implements VerificationCodeRepository {

  @Getter(AccessLevel.PROTECTED)
  private VerificationCodeConverter converter;

  public VerificationCodeRepositoryImpl(
      VerificationCodeTunnel verificationCodeTunnel,
      ValidateCodeMapper mapper,
      VerificationCodeConverter converter) {
    super(verificationCodeTunnel, mapper);
    this.converter = converter;
  }

  @Override
  public Optional<VerificationCode> findValidCodeByPhoneNumber(String phoneNumber, String code) {
    return findValidCode(NoticeSendChannel.PHONE, phoneNumber, code);
  }

  @Override
  public Optional<VerificationCode> findValidCodeByEmail(String email, String code) {
    return findValidCode(NoticeSendChannel.EMAIL, email, code);
  }

  @Override
  public Optional<VerificationCode> findValidCode(String sendDst, String code, NoticeType type) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<VerificationCodeDO>()
                        .eq(VerificationCodeDO::getSendDst, sendDst)
                        .eq(VerificationCodeDO::getType, type)
                        .eq(VerificationCodeDO::getCode, code)
                        .isNull(VerificationCodeDO::getVerifiedAt)
                        .gt(VerificationCodeDO::getExpireAt, LocalDateTime.now())))
        .map(getConverter()::convertFrom);
  }

  private Optional<VerificationCode> findValidCode(
      NoticeSendChannel channel, String sendDst, String code) {
    return Optional.ofNullable(
            getTunnel()
                .getOne(
                    new LambdaQueryWrapper<VerificationCodeDO>()
                        .eq(VerificationCodeDO::getChannel, channel)
                        .eq(VerificationCodeDO::getSendDst, sendDst)
                        .eq(VerificationCodeDO::getCode, code)
                        .isNull(VerificationCodeDO::getVerifiedAt)
                        .gt(VerificationCodeDO::getExpireAt, LocalDateTime.now())))
        .map(getConverter()::convertFrom);
  }
}

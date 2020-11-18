package wx.domain.infra.verificationcode;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.common.data.shared.id.VerificationCodeId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationCode extends IdBasedEntity<VerificationCodeId, VerificationCode> {

  private @Nullable UserId userId;

  private String code;

  private NoticeType type;

  private NoticeSendChannel sendChannel;

  private String sendDst;

  private LocalDateTime verifiedAt;

  private LocalDateTime sentAt;

  private LocalDateTime expireAt;

  public VerificationCode(
      VerificationCodeId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      @Nullable UserId userId,
      String code,
      NoticeType type,
      NoticeSendChannel sendChannel,
      String sendDst,
      LocalDateTime verifiedAt,
      LocalDateTime sentAt,
      LocalDateTime expireAt) {
    super(id, tenantId, createdAt, updatedAt);
    this.userId = userId;
    this.code = code;
    this.type = type;
    this.sendChannel = sendChannel;
    this.sendDst = sendDst;
    this.verifiedAt = verifiedAt;
    this.sentAt = sentAt;
    this.expireAt = expireAt;
  }

  public VerificationCode(
      TenantId tenantId,
      @Nullable UserId userId,
      String code,
      NoticeType type,
      NoticeSendChannel sendChannel,
      String sendDst,
      LocalDateTime verifiedAt,
      LocalDateTime sentAt,
      LocalDateTime expireAt) {
    super(tenantId);
    this.userId = userId;
    this.code = code;
    this.type = type;
    this.sendChannel = sendChannel;
    this.sendDst = sendDst;
    this.verifiedAt = verifiedAt;
    this.sentAt = sentAt;
    this.expireAt = expireAt;
  }
}

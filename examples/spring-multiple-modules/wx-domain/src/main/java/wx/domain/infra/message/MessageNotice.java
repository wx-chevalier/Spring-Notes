package wx.domain.infra.message;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.EntityType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.MessageNoticeId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageNotice extends IdBasedEntity<MessageNoticeId, MessageNotice> {

  private NoticeTypeKind kind;

  private NoticeType type;

  private NoticeSendChannel channel;

  private ApplicationId app;

  private String entityId;

  private EntityType entityType;

  private String content;

  private String title;

  private Boolean hasRead;

  private UserId userId;

  public MessageNotice(
      NoticeTypeKind kind,
      NoticeType type,
      NoticeSendChannel channel,
      ApplicationId app,
      String content,
      String title,
      String entityId,
      EntityType entityType,
      UserId userId) {
    this.kind = kind;
    this.type = type;
    this.channel = channel;
    this.app = app;
    this.content = content;
    this.title = title;
    this.entityId = entityId;
    this.entityType = entityType;
    this.userId = userId;
  }

  public MessageNotice(
      NoticeTypeKind kind,
      NoticeType type,
      NoticeSendChannel channel,
      ApplicationId app,
      String content,
      String title,
      String entityId,
      EntityType entityType,
      UserId userId,
      MessageNoticeId id,
      TenantId tenantId,
      Boolean hasRead,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    super(id, tenantId, createdAt, updatedAt);
    this.kind = kind;
    this.type = type;
    this.channel = channel;
    this.app = app;
    this.content = content;
    this.title = title;
    this.hasRead = hasRead;
    this.entityId = entityId;
    this.entityType = entityType;
    this.userId = userId;
  }

  public MessageNotice(
      NoticeTypeKind kind,
      NoticeType type,
      NoticeSendChannel channel,
      ApplicationId app,
      String content,
      String title,
      Boolean hasRead,
      String entityId,
      EntityType entityType,
      UserId userId) {
    this.kind = kind;
    this.type = type;
    this.channel = channel;
    this.app = app;
    this.content = content;
    this.title = title;
    this.entityId = entityId;
    this.entityType = entityType;
    this.userId = userId;
    this.hasRead = hasRead;
  }
}

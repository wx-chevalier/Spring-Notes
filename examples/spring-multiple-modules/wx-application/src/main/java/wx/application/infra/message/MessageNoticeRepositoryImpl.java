package wx.application.infra.message;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.EntityType;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.MessageNoticeId;
import wx.common.data.shared.id.UserId;
import wx.domain.admin.ApplicationRepository;
import wx.domain.infra.message.MessageNotice;
import wx.domain.infra.message.MessageNoticeRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.infra.message.MessageNoticeDO;
import wx.infra.tunnel.db.infra.message.MessageNoticeTunnel;
import wx.infra.tunnel.db.mapper.infra.message.MessageNoticeMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Slf4j
@Service
public class MessageNoticeRepositoryImpl
    extends MyBatisIdBasedEntityRepository<
        MessageNoticeTunnel, MessageNoticeMapper, MessageNoticeDO, MessageNotice, MessageNoticeId>
    implements MessageNoticeRepository {

  @Getter(AccessLevel.PROTECTED)
  private MessageNoticeConverter converter;

  public MessageNoticeRepositoryImpl(
      MessageNoticeTunnel messageTypeTunnel,
      MessageNoticeMapper mapper,
      MessageNoticeConverter converter,
      ApplicationRepository applicationRepository) {
    super(messageTypeTunnel, mapper);
    this.converter = converter;
  }

  @Override
  @Transactional
  public boolean exists(
      NoticeType noticeType, NoticeSendChannel channel, BaseEntityId entityId, UserId userId) {

    Long entityLongId = Optional.ofNullable(entityId).map(BaseEntityId::getId).orElse(null);
    EntityType entityType =
        Optional.ofNullable(entityId).map(BaseEntityId::getEntityType).orElse(null);

    Wrapper<MessageNoticeDO> countWrapper =
        new LambdaQueryWrapper<MessageNoticeDO>()
            .eq(MessageNoticeDO::getType, noticeType)
            .eq(MessageNoticeDO::getSendChannel, channel)
            .eq(Objects.nonNull(entityLongId), MessageNoticeDO::getEntityId, entityLongId)
            .eq(Objects.nonNull(entityType), MessageNoticeDO::getEntityType, entityType)
            .eq(MessageNoticeDO::getUserId, userId.getId())
            .ge(BaseDO::getCreatedAt, LocalDateTime.now().plusHours(-1));
    return getTunnel().count(countWrapper) != 0;
  }
}

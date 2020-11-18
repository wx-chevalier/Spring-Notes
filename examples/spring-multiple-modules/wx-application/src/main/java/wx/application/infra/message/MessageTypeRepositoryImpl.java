package wx.application.infra.message;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.MessageTypeId;
import wx.domain.admin.Application;
import wx.domain.admin.ApplicationRepository;
import wx.domain.infra.message.MessageType;
import wx.domain.infra.message.MessageTypeRepository;
import wx.infra.common.exception.NotFoundException;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.infra.message.MessageTypeDO;
import wx.infra.tunnel.db.infra.message.MessageTypeTunnel;
import wx.infra.tunnel.db.mapper.infra.message.MessageTypeMapper;

@Slf4j
@Service
public class MessageTypeRepositoryImpl
    extends MyBatisIdBasedEntityRepository<
        MessageTypeTunnel, MessageTypeMapper, MessageTypeDO, MessageType, MessageTypeId>
    implements MessageTypeRepository {

  @Getter(AccessLevel.PROTECTED)
  private MessageTypeConverter converter;

  private ApplicationRepository applicationRepository;

  public MessageTypeRepositoryImpl(
      MessageTypeTunnel messageTypeTunnel,
      MessageTypeMapper mapper,
      MessageTypeConverter converter,
      ApplicationRepository applicationRepository) {
    super(messageTypeTunnel, mapper);
    this.converter = converter;
    this.applicationRepository = applicationRepository;
  }

  @Override
  public Optional<MessageType> getByNoticeType(NoticeType type) {

    MessageTypeDO messageTypeDO = this.getTunnel().getByNoticeType(type);
    if (messageTypeDO == null) {
      throw new NotFoundException("查询错误,消息类型不存在");
    }

    // 查询应用信息
    Application app = null;

    if (messageTypeDO.getAppId() != null) {
      app =
          applicationRepository
              .findById(TenantId.NULL_TENANT_ID, new ApplicationId(messageTypeDO.getAppId()))
              .orElseThrow(() -> new NotFoundException("应用信息不存在"));
    }

    Application finalApp = app;
    return Optional.of(messageTypeDO)
        .map(converter::convertFrom)
        .map(messageType -> messageType.setApp(finalApp));
  }
}

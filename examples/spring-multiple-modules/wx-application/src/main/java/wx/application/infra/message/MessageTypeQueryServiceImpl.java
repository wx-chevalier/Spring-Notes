package wx.application.infra.message;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.infra.message.MessageTemplate;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.*;
import wx.domain.infra.message.MessageType;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.infra.message.MessageTypeDO;
import wx.infra.tunnel.db.infra.message.MessageTypeTunnel;
import wx.infra.tunnel.db.shared.BaseDO;

@Slf4j
@Service
public class MessageTypeQueryServiceImpl implements MessageTypeQueryService {

  private MessageTypeTunnel messageTypeTunnel;

  private MessageTypeConverter converter;

  public MessageTypeQueryServiceImpl(
      MessageTypeTunnel messageTypeTunnel, MessageTypeConverter converter) {
    this.messageTypeTunnel = messageTypeTunnel;
    this.converter = converter;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageType> getByAppId(ApplicationId appId) {
    Wrapper<MessageTypeDO> queryWrapper =
        Helper.getQueryWrapper(MessageTypeDO.class)
            .eq(Objects.nonNull(appId), MessageTypeDO::getAppId, appId.getId())
            .isNull(BaseDO::getDeletedAt);

    return messageTypeTunnel.list(queryWrapper).stream()
        .map(converter::convertFrom)
        .collect(Collectors.toList());
  }

  @Override
  public Map<NoticeSendChannel, String> getAndParseByNoticeType(NoticeType noticeType) {

    MessageTemplate template =
        Optional.ofNullable(messageTypeTunnel.getByNoticeType(noticeType))
            .map(converter::convertFrom)
            .map(MessageType::getTemplate)
            .orElse(null);

    if (template == null) {
      log.info("消息类型[type={}]不存在,无法获取到消息模板", noticeType.name());
      throw new NotFoundException("消息发送失败,消息类型模板不存在");
    }
    Map<NoticeSendChannel, String> templateMap = new HashMap<>(3);
    templateMap.put(NoticeSendChannel.SMS, template.getSmsTemplateId());
    templateMap.put(NoticeSendChannel.EMAIL, template.getEmailTemplate());
    templateMap.put(NoticeSendChannel.WECHAT, template.getWechatTemplateId());
    return templateMap;
  }

  @Override
  public boolean checkKindMatch(List<MessageTypeId> messageTypeIds, NoticeTypeKind kind) {
    return messageTypeTunnel.match(messageTypeIds, kind);
  }

  @Override
  public boolean exist(List<MessageTypeId> messageTypeIdList) {
    return messageTypeTunnel.count(messageTypeIdList) == messageTypeIdList.size();
  }
}

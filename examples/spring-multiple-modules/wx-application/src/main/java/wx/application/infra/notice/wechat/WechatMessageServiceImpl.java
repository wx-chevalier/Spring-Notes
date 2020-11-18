package wx.application.infra.notice.wechat;

import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.infra.message.MessageTemplate;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.UserId;
import wx.domain.account.UserRepository;
import wx.domain.infra.message.MessageType;
import wx.domain.infra.message.MessageTypeRepository;
import wx.domain.wechat.WechatAccessToken;
import wx.domain.wechat.WechatAccessTokenRepository;
import wx.infra.tunnel.wechat.WechatTunnel;

@Slf4j
@Service
public class WechatMessageServiceImpl implements WechatMessageService {

  @Getter MessageTypeRepository messageTypeRepository;

  @Getter UserRepository userRepository;

  private WechatAccessTokenRepository wechatAccessTokenRepository;

  public WechatMessageServiceImpl(
      MessageTypeRepository messageTypeRepository,
      UserRepository userRepository,
      WechatAccessTokenRepository wechatAccessTokenRepository) {
    this.messageTypeRepository = messageTypeRepository;
    this.userRepository = userRepository;
    this.wechatAccessTokenRepository = wechatAccessTokenRepository;
  }

  @Override
  public String sentBindNotice(UserId userId, String target, String password) {
    throw new RuntimeException();
  }

  @Override
  public void send(String dst, String subject, NoticeType noticeType, Map<String, String> param) {

    WechatAccessToken accessToken = wechatAccessTokenRepository.getLatest();
    String wechatTemplateId =
        Optional.ofNullable(getMessageTypeById(noticeType))
            .map(MessageType::getTemplate)
            .map(MessageTemplate::getWechatTemplateId)
            .orElse(null);
    if (wechatTemplateId == null || wechatTemplateId.length() == 0) {
      log.error("消息类型:[{}]的微信模板暂未配置，取消消息发送", noticeType);
      return;
    }
    WechatTunnel.sendTemplateMessage(accessToken.getAccessToken(), dst, wechatTemplateId, param);
  }
}

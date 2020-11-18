package wx.application.infra.message;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import wx.application.infra.notice.email.EmailService;
import wx.application.infra.notice.sms.SmsService;
import wx.application.infra.notice.wechat.WechatMessageService;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.mq.notice.SendNoticeMessage;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;
import wx.domain.account.UserRepository;
import wx.domain.admin.Application;
import wx.domain.infra.message.MessageNotice;
import wx.domain.infra.message.MessageNoticeRepository;
import wx.domain.infra.message.MessageType;
import wx.domain.infra.message.MessageTypeRepository;
import wx.domain.wechat.WechatUser;
import wx.domain.wechat.WechatUserInfoRepository;
import wx.infra.common.exception.NotFoundException;

@Slf4j
@Service
public class NoticeMessageHandleServiceImpl implements NoticeMessageHandleService {

  private MessageTypeRepository messageTypeRepository;

  private MessageNoticeRepository messageNoticeRepository;

  private RoleMessageConfigQueryService roleMessageConfigQueryService;

  private UserRepository userRepository;

  private EmailService emailService;

  private SmsService smsService;

  private WechatMessageService wechatMessageService;

  private WechatUserInfoRepository wechatUserInfoRepository;

  public NoticeMessageHandleServiceImpl(
      MessageTypeRepository messageTypeRepository,
      MessageNoticeRepository messageNoticeRepository,
      RoleMessageConfigQueryService roleMessageConfigQueryService,
      UserRepository userRepository,
      EmailService emailService,
      SmsService smsService,
      WechatMessageService wechatMessageService,
      WechatUserInfoRepository wechatUserInfoRepository) {
    this.messageTypeRepository = messageTypeRepository;
    this.messageNoticeRepository = messageNoticeRepository;
    this.roleMessageConfigQueryService = roleMessageConfigQueryService;
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.smsService = smsService;
    this.wechatMessageService = wechatMessageService;
    this.wechatUserInfoRepository = wechatUserInfoRepository;
  }

  @Override
  public void handle(@NotNull SendNoticeMessage msg) {
    log.info("接收到发送消息的Msg:{}", msg);

    MessageType messageType =
        messageTypeRepository.getByNoticeType(msg.getNoticeType()).orElse(null);
    if (messageType == null) {
      log.info("不存在类型为:{}的消息配置，请检查配置后重试", msg.getNoticeType());
      return;
    }

    // 查询需要发送的用户列表
    List<UserId> targetUserIds =
        roleMessageConfigQueryService.getTargetUserIds(msg.getTenantId(), messageType.getId());

    // 根据用户和消息类型查询其应当发送的渠道
    List<NoticeSendChannel> sendChannels =
        roleMessageConfigQueryService.getSendChannelByTenantId(
            msg.getTenantId(), messageType.getId());

    log.info("本息消息发送配置=> 用户列表:{} 发送渠道:{}", targetUserIds, sendChannels);

    for (UserId userId : targetUserIds) {
      sendChannels
          .parallelStream()
          .forEach(
              channel ->
                  send(
                      channel, userId, msg.getNoticeType(), msg.getBaseEntityId(), msg.getParam()));
    }
  }

  /**
   * 根据发送渠道的不同，将消息分发到不同的服务
   *
   * @param channel 发送渠道
   * @param userId 用户详情
   * @param noticeType 发送模板(对于Email而言是html源码，对于weChat和Sms则是模板ID)
   * @param param 模板的替换参数(具体的替换逻辑在不同的服务实现各自实现)
   */
  @Override
  public void send(
      NoticeSendChannel channel,
      UserId userId,
      NoticeType noticeType,
      BaseEntityId entityId,
      Map<String, String> param) {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      log.info("尝试通过渠道:[{}] 向用户:[{}] 发送:[{}] 但用户不存在", channel, userId, noticeType);
      return;
    }
    // 检查详细是否已经发送

    switch (channel) {
      case EMAIL:
        if (StringUtils.isEmpty(user.getEmail())) {
          log.info("用户:[id={}] 邮箱未配置，取消发送邮件消息", user);
          return;
        }
        emailService.send(user.getEmail(), noticeType, param);
        break;
      case WECHAT:
        Optional<WechatUser> wechatUserOptional = wechatUserInfoRepository.getByUserId(userId);
        if (!wechatUserOptional.isPresent()) {
          log.info("用户:[id={}] 微信未配置，取消发送微信消息", user);
          return;
        }
        wechatMessageService.send(wechatUserOptional.get().getOpenId(), noticeType, param);
        break;
      case SMS:
        if (StringUtils.isEmpty(user.getPhoneNumber())) {
          log.info("用户:[id={}] 手机号码未配置，取消发送短信消息", user);
          return;
        }
        smsService.send(user.getPhoneNumber(), noticeType, param);
        break;
      default:
        log.info("发送失败,目前消息发送仅支持邮件/微信/短信");
        return;
    }
    log.info("发送通知:[{}]到用户:[{}]完成", channel, userId);
  }

  @Override
  public void send(
      NoticeSendChannel channel,
      String dest,
      NoticeType noticeType,
      BaseEntityId entityId,
      Map<String, String> param) {
    if (!StringUtils.hasText(dest)) {
      log.info("消息发送目标为空，无法发送");
      return;
    }
    switch (channel) {
      case EMAIL:
        emailService.send(dest, noticeType, param);
        break;
      case WECHAT:
        wechatMessageService.send(dest, noticeType, param);
        break;
      case SMS:
        smsService.send(dest, noticeType, param);
        break;
      default:
        log.info("发送失败,目前消息发送仅支持邮件/微信以及短信");
        return;
    }
    log.info("发送通知:[{}]到:[{}]完成", channel, dest);

    MessageType messageType =
        messageTypeRepository
            .getByNoticeType(noticeType)
            .orElseThrow(
                () -> new NotFoundException(String.format("消息类型type=[%s]不存在", noticeType)));

    Application app = messageType.getApp();
    // 持久化消息发送记录
    MessageNotice notice =
        new MessageNotice(
            messageType.getKind(),
            noticeType,
            channel,
            app == null ? null : app.getId(),
            param.toString(),
            noticeType.getDesc(),
            null,
            null,
            null);
    messageNoticeRepository.save(TenantId.NULL_TENANT_ID, notice);
  }
}

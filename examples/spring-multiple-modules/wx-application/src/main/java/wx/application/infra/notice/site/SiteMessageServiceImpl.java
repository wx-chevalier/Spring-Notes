package wx.application.infra.notice.site;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.application.infra.message.MessageNoticeCount;
import wx.common.data.code.ApiErrorCode;
import wx.common.data.infra.message.MessageTemplate;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.*;
import wx.domain.infra.message.MessageNotice;
import wx.domain.infra.message.MessageType;
import wx.domain.infra.message.MessageTypeRepository;
import wx.infra.common.exception.NotFoundException;
import wx.infra.common.util.StringUtil;
import wx.infra.converter.PageConverter;
import wx.infra.tunnel.db.account.UserDO;
import wx.infra.tunnel.db.account.UserTunnel;
import wx.infra.tunnel.db.infra.message.MessageNoticeDO;
import wx.infra.tunnel.db.infra.message.MessageNoticeTunnel;
import wx.infra.tunnel.db.shared.BaseDO;
import wx.infra.tunnel.db.shared.KvDO;

@Slf4j
@Service
public class SiteMessageServiceImpl implements SiteMessageService {

  private MessageTypeRepository messageTypeRepository;

  private SiteMessageConverter siteMessageConverter;

  private UserTunnel userTunnel;

  private MessageNoticeTunnel messageNoticeTunnel;

  public SiteMessageServiceImpl(
      MessageTypeRepository messageTypeRepository,
      SiteMessageConverter siteMessageConverter,
      UserTunnel userTunnel,
      MessageNoticeTunnel messageNoticeTunnel) {
    this.messageTypeRepository = messageTypeRepository;
    this.siteMessageConverter = siteMessageConverter;
    this.userTunnel = userTunnel;
    this.messageNoticeTunnel = messageNoticeTunnel;
  }

  @Override
  @Transactional
  public void send(
      UserId userId, NoticeType type, BaseEntityId baseEntityId, Map<String, String> param) {
    // 获取站内信消息的模板
    Optional<MessageType> noticeType = messageTypeRepository.getByNoticeType(type);
    String siteMessageTemplate =
        noticeType
            .map(MessageType::getTemplate)
            .map(MessageTemplate::getSiteMessageTemplate)
            .orElseThrow(() -> new NotFoundException("站内信模板不存在"));

    String siteMessageContent = StringUtil.replaceAll(siteMessageTemplate, param);

    MessageType messageType = noticeType.get();
    ApplicationId applicationId =
        (ApplicationId) Optional.ofNullable(messageType.getApp().getId()).orElse(null);
    messageNoticeTunnel.save(
        userId,
        type,
        messageType.getKind(),
        NoticeSendChannel.SITE,
        applicationId,
        baseEntityId,
        siteMessageContent);
  }

  @Override
  @Transactional
  public void send(
      TenantId tenantId,
      final NoticeType type,
      @Nullable final BaseEntityId baseEntityId,
      Map<String, String> param) {

    // 获取租户下的所有用户
    Collection<UserDO> userDOList =
        userTunnel.list(
            new LambdaQueryWrapper<UserDO>()
                .eq(BaseDO::getTenantId, tenantId.getId())
                .isNull(BaseDO::getDeletedAt));

    // 分别调用发送站内信服务
    userDOList.stream()
        .map(BaseDO::getId)
        .map(UserId::new)
        .forEach(userId -> send(userId, type, baseEntityId, param));
  }

  @Override
  public org.springframework.data.domain.Page<MessageNotice> search(
      UserId userId,
      Pageable pageable,
      ApplicationId appId,
      NoticeTypeKind kind,
      Boolean hasRead,
      String searchKey,
      LocalDate startDate,
      LocalDate endDate) {

    Function<LocalDate, LocalDateTime> getMinDatetime =
        date -> LocalDateTime.of(date, LocalTime.MIN);

    LocalDateTime startDatetime = Optional.ofNullable(startDate).map(getMinDatetime).orElse(null);
    LocalDateTime endDatetime = Optional.ofNullable(endDate).map(getMinDatetime).orElse(null);

    // 分页信息
    IPage<MessageNoticeDO> messageNoticeDoList =
        messageNoticeTunnel.get(
            userId,
            pageable,
            appId,
            kind,
            NoticeSendChannel.SITE,
            hasRead,
            searchKey,
            startDatetime,
            endDatetime);

    // 封装转换数据
    List<MessageNotice> coList =
        messageNoticeDoList.getRecords().stream()
            .map(siteMessageConverter::convertFrom)
            .collect(Collectors.toList());

    Page<MessageNotice> messageNoticeDOPage =
        new Page<MessageNotice>().setTotal(messageNoticeDoList.getTotal()).setRecords(coList);

    return PageConverter.toPage(messageNoticeDOPage, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageNoticeCount countOfSiteMessage(UserId userId, ApplicationId appId) {

    // 查询数据
    List<KvDO<Boolean, Long>> kvDOS = messageNoticeTunnel.groupStatistics(userId, appId);
    Map<Boolean, Long> longMap =
        kvDOS.stream().collect(Collectors.toMap(KvDO::getKey, KvDO::getValue));

    // 封装数据
    return new MessageNoticeCount()
        .setUnreadCount(longMap.getOrDefault(Boolean.FALSE, 0L))
        .setReadCount(longMap.getOrDefault(Boolean.TRUE, 0L));
  }

  @Override
  @Transactional
  public void markedRead(UserId userId, Collection<Long> messageNoticeIds) {
    messageNoticeTunnel.deleteByIds(userId, messageNoticeIds);
  }

  @Override
  @Transactional
  public void markedRead(UserId userId, Set<ApplicationId> applicationIds) {
    messageNoticeTunnel.markRead(userId, applicationIds);
  }

  @Override
  @Transactional
  public void markedRead(Collection<Long> messageNoticeIds) {
    messageNoticeTunnel.markRead(messageNoticeIds);
  }

  @Override
  public MessageNotice findById(UserId userId, MessageNoticeId messageNoticeId) {
    MessageNoticeDO messageNoticeDO = this.messageNoticeTunnel.getById(messageNoticeId.getId());
    return Optional.ofNullable(messageNoticeDO)
        .map(siteMessageConverter::convertFrom)
        .orElseThrow(() -> new NotFoundException("查询失败,此消息不存在", ApiErrorCode.NOT_FOUND));
  }
}

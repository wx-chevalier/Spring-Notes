package wx.infra.tunnel.db.infra.message;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.EntityType;
import wx.common.data.shared.id.*;
import wx.infra.converter.PageConverter;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.message.MessageNoticeMapper;
import wx.infra.tunnel.db.shared.KvDO;

@Component
public class MessageNoticeTunnel extends ServiceImpl<MessageNoticeMapper, MessageNoticeDO> {

  /** 根据条件查询消息 */
  public IPage<MessageNoticeDO> get(
      UserId userId,
      Pageable pageable,
      ApplicationId appId,
      NoticeTypeKind kind,
      NoticeSendChannel channel,
      Boolean hasRead,
      String searchKey,
      LocalDateTime startDatetime,
      LocalDateTime endDatetime) {

    // 分页信息
    IPage<MessageNoticeDO> page = PageConverter.toIPage(pageable);

    Long appIdLong = Optional.ofNullable(appId).map(ApplicationId::getId).orElse(null);

    // 查询条件
    Wrapper<MessageNoticeDO> queryWrapper =
        Helper.getQueryWrapper(MessageNoticeDO.class)
            .eq(MessageNoticeDO::getUserId, userId.getId())
            .eq(Objects.nonNull(appIdLong), MessageNoticeDO::getAppId, appIdLong)
            .eq(Objects.nonNull(channel), MessageNoticeDO::getSendChannel, channel)
            .eq(Objects.nonNull(kind), MessageNoticeDO::getKind, kind)
            .in(MessageNoticeDO::getKind, NoticeTypeKind.getSiteType())
            .eq(Objects.nonNull(hasRead), MessageNoticeDO::getIsRead, hasRead)
            .ge(Objects.nonNull(startDatetime), MessageNoticeDO::getCreatedAt, startDatetime)
            .le(Objects.nonNull(endDatetime), MessageNoticeDO::getCreatedAt, endDatetime)
            .isNull(MessageNoticeDO::getDeletedAt)
            .and(
                StringUtils.hasText(searchKey),
                wrapper ->
                    wrapper
                        .like(MessageNoticeDO::getTitle, searchKey)
                        .or()
                        .like(MessageNoticeDO::getContent, searchKey))
            .orderByDesc(MessageNoticeDO::getCreatedAt);

    return this.baseMapper.selectPage(page, queryWrapper);
  }

  /** 按组划分消息 */
  public List<KvDO<Boolean, Long>> groupStatistics(UserId userId, ApplicationId appId) {
    Long appIdLong = Optional.ofNullable(appId).map(BaseEntityId::getId).orElse(null);
    return this.baseMapper.groupStatistics(userId.getId(), appIdLong, NoticeTypeKind.getSiteType());
  }

  /** 标记某个消息通知已读 */
  public void markRead(Collection<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return;
    }

    MessageNoticeDO entity = new MessageNoticeDO();
    entity.setIsRead(Boolean.TRUE).setUpdatedAt(LocalDateTime.now());

    Wrapper<MessageNoticeDO> wrapper =
        Helper.getUpdateWrapper(MessageNoticeDO.class)
            .in(MessageNoticeDO::getId, ids)
            .isNull(MessageNoticeDO::getDeletedAt);
    this.baseMapper.update(entity, wrapper);
  }

  /** 逻辑删除指定消息 */
  public void remove(UserId userId, Collection<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return;
    }
    MessageNoticeDO entity = new MessageNoticeDO();
    entity.setDeletedAt(LocalDateTime.now());
    Wrapper<MessageNoticeDO> wrapper =
        Helper.getUpdateWrapper(MessageNoticeDO.class)
            .eq(MessageNoticeDO::getUserId, userId.getId())
            .in(MessageNoticeDO::getId, ids)
            .isNull(MessageNoticeDO::getDeletedAt);
    this.baseMapper.update(entity, wrapper);
  }

  public Boolean exist(Long userId, Collection<Long> ids, List<NoticeTypeKind> channelTypes) {
    Wrapper<MessageNoticeDO> queryWrapper =
        Helper.getQueryWrapper(MessageNoticeDO.class)
            .eq(MessageNoticeDO::getUserId, userId)
            .in(MessageNoticeDO::getSendChannel, channelTypes)
            .in(MessageNoticeDO::getId, ids)
            .isNull(MessageNoticeDO::getDeletedAt);
    return this.count(queryWrapper) == ids.size();
  }

  public void save(
      UserId userId,
      NoticeType type,
      NoticeTypeKind kind,
      NoticeSendChannel channel,
      @Nullable ApplicationId appId,
      @Nullable BaseEntityId entityId,
      String siteMessageContent) {

    MessageNoticeDO entity = new MessageNoticeDO();
    entity
        .setUserId(userId.getId())
        .setIsRead(Boolean.FALSE)
        .setContent(siteMessageContent)
        .setSendChannel(channel)
        .setType(type)
        .setKind(kind)
        .setTitle(type.getDesc())
        .setAppId(appId == null ? null : appId.getId())
        .setEntityId(entityId == null ? null : entityId.getId())
        .setEntityType(entityId == null ? null : entityId.getEntityType());
    this.baseMapper.insert(entity);
  }

  /** 批量逻辑删除指定消息 */
  public void deleteByIds(UserId userId, Collection<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return;
    }

    MessageNoticeDO entity = new MessageNoticeDO();
    entity.setDeletedAt(LocalDateTime.now());

    Wrapper<MessageNoticeDO> updateWrapper =
        Helper.getUpdateWrapper(MessageNoticeDO.class)
            .eq(MessageNoticeDO::getUserId, userId.getId())
            .in(MessageNoticeDO::getId, ids)
            .isNull(MessageNoticeDO::getDeletedAt);
    this.baseMapper.update(entity, updateWrapper);
  }

  public int count(BaseEntityId entityId, NoticeType noticeType) {
    Long entityLongId = Optional.ofNullable(entityId).map(BaseEntityId::getId).orElse(null);
    EntityType entityType =
        Optional.ofNullable(entityId).map(BaseEntityId::getEntityType).orElse(null);

    Wrapper<MessageNoticeDO> queryWrapper =
        Helper.getQueryWrapper(MessageNoticeDO.class)
            .eq(Objects.nonNull(entityLongId), MessageNoticeDO::getEntityId, entityLongId)
            .eq(Objects.nonNull(entityType), MessageNoticeDO::getEntityType, entityType)
            .in(MessageNoticeDO::getType, noticeType)
            .isNull(MessageNoticeDO::getDeletedAt);
    return this.count(queryWrapper);
  }

  /** 标记指定的应用中消息已读 */
  public void markRead(UserId userId, Collection<ApplicationId> applicationIds) {
    if (CollectionUtils.isEmpty(applicationIds)) {
      return;
    }

    Wrapper<MessageNoticeDO> updateWrapper =
        Helper.getUpdateWrapper(MessageNoticeDO.class)
            .eq(MessageNoticeDO::getUserId, userId.getId())
            .eq(MessageNoticeDO::getSendChannel, NoticeSendChannel.SITE)
            .in(
                MessageNoticeDO::getAppId,
                applicationIds.stream().map(ApplicationId::getId).collect(Collectors.toSet()))
            .isNull(MessageNoticeDO::getDeletedAt)
            .set(MessageNoticeDO::getIsRead, Boolean.TRUE);
    this.update(updateWrapper);
  }
}

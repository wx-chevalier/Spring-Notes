package wx.application.infra.notice.site;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wx.application.infra.message.MessageNoticeCount;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.*;
import wx.domain.infra.message.MessageNotice;

public interface SiteMessageService {

  /**
   * 发送站内信到指定用户
   *
   * @param userId 用户ID
   * @param type 消息类型
   * @param entityId 关联的实体ID
   * @param param 站内信的参数信息
   */
  void send(UserId userId, NoticeType type, BaseEntityId entityId, Map<String, String> param);

  /**
   * 发送站内信到指定租户下的所有用户
   *
   * @param tenantId 租户ID
   * @param type 消息类型
   * @param entityId 关联实体ID
   * @param param 参数信息
   */
  void send(TenantId tenantId, NoticeType type, BaseEntityId entityId, Map<String, String> param);

  /** 搜索指定用户的站内消息 */
  Page<MessageNotice> search(
      UserId userId,
      Pageable pageable,
      ApplicationId appId,
      NoticeTypeKind kind,
      Boolean hasRead,
      String searchKey,
      LocalDate startDate,
      LocalDate endDate);

  /** 统计站内信的数目信息 */
  MessageNoticeCount countOfSiteMessage(UserId userId, ApplicationId appId);

  /** 删除消息 */
  void markedRead(UserId userId, Collection<Long> messageNoticeIds);

  /** 标记为已读 */
  void markedRead(Collection<Long> messageNoticeIds);

  /** 标记应用消息全部已读{@param applicationIds} */
  void markedRead(UserId userId, Set<ApplicationId> applicationIds);

  MessageNotice findById(UserId userId, MessageNoticeId messageNoticeId);
}

package wx.application.infra.message;

import java.util.List;
import java.util.Map;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.ApplicationId;
import wx.common.data.shared.id.MessageTypeId;
import wx.domain.infra.message.MessageType;

public interface MessageTypeQueryService {

  /** 获取所有的消息类型，并以消息类型种类(任务类，警告类，错误类)分组 */
  List<MessageType> getByAppId(ApplicationId appId);

  /**
   * 根据消息类型获取消息模板并解析模板
   *
   * <p>KEY: 消息的发送渠道 VALUE: 消息的模板(html or templateId)</>
   */
  Map<NoticeSendChannel, String> getAndParseByNoticeType(NoticeType noticeType);

  /**
   * 校验给定的消息类型存在并且和给定的消息种类匹配
   *
   * @param messageTypeIds 消息的集合IDs
   * @param kind 消息的种类
   * @return 返回是否匹配
   */
  boolean checkKindMatch(List<MessageTypeId> messageTypeIds, NoticeTypeKind kind);

  /** 检查消息类型是否均存在 */
  boolean exist(List<MessageTypeId> messageTypeIdList);
}

package wx.application.infra.notice;

import java.util.Map;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;
import wx.domain.account.UserRepository;
import wx.domain.infra.message.MessageType;
import wx.domain.infra.message.MessageTypeRepository;
import wx.infra.common.exception.NotFoundException;

public interface NoticeService {

  /**
   * 发送绑定通知
   *
   * @param userId 发送人ID
   * @param password 当前登录用户的密码
   * @param target 目标
   * @return 返回生成的验证码
   */
  String sentBindNotice(UserId userId, String target, String password);

  /**
   * 发送服务
   *
   * @param dst 目标用户
   * @param subject 主题
   * @param noticeType 通知类型
   * @param param 消息通知的参数信息
   */
  void send(String dst, String subject, NoticeType noticeType, Map<String, String> param);

  /**
   * 发送服务
   *
   * @param dst 目标
   * @param noticeType 消息类型ID
   * @param param 消息通知的参数信息
   */
  default void send(String dst, NoticeType noticeType, Map<String, String> param) {
    send(dst, noticeType.getDesc(), noticeType, param);
  }

  // region 用于获取用户和消息类型信息
  UserRepository getUserRepository();

  MessageTypeRepository getMessageTypeRepository();

  default MessageType getMessageTypeById(NoticeType noticeType) {
    return getMessageTypeRepository()
        .getByNoticeType(noticeType)
        .orElseThrow(() -> new NotFoundException(String.format("消息类型type=[%s]不存在", noticeType)));
  }

  default User getUserById(UserId userId) {
    return getUserRepository()
        .findById(userId)
        .orElseThrow(() -> new NotFoundException(String.format("用户id=[%s]不存在", userId)));
  }
  // endregion
}

package wx.domain.event;

public interface EventBus {

  /**
   * 事件发送失败应直接抛出异常
   *
   * @param event 领域事件
   */
  void send(Event event);
}

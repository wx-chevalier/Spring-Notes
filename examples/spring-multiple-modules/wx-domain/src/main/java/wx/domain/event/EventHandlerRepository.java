package wx.domain.event;

import java.util.Optional;

public interface EventHandlerRepository {

  /** 获取事件的处理器 */
  <E extends Event> Optional<EventHandler<E>> getEventHandler(Class<E> eventClass);
}

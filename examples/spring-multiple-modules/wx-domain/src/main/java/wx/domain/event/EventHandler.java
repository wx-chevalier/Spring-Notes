package wx.domain.event;

public interface EventHandler<E extends Event> {

  Class<E> eventClass();

  void handle(E event);
}

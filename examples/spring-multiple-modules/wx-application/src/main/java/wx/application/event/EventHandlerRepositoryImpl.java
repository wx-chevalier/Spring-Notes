package wx.application.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import wx.domain.event.Event;
import wx.domain.event.EventHandler;
import wx.domain.event.EventHandlerRepository;

@Slf4j
@Component
public class EventHandlerRepositoryImpl implements EventHandlerRepository, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @SuppressWarnings("rawtypes")
  private Map<Class, EventHandler> handlers;

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <E extends Event> Optional<EventHandler<E>> getEventHandler(
      Class<E> eventClass) {
    if (handlers == null) {
      this.handlers = scanHandlers();
    }
    return Optional.ofNullable(handlers.get(eventClass));
  }

  @SuppressWarnings("rawtypes")
  private Map<Class, EventHandler> scanHandlers() {
    Map<Class, EventHandler> handlers = new HashMap<>();
    for (EventHandler eventHandler :
        applicationContext.getBeansOfType(EventHandler.class).values()) {
      if (eventHandler.eventClass() == null) {
        throw new RuntimeException("EventHandler::eventClass not set: " + eventHandler.getClass());
      }
      handlers.put(eventHandler.eventClass(), eventHandler);
    }
    return handlers;
  }
}

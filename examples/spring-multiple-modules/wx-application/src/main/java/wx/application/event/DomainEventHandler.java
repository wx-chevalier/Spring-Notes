package wx.application.event;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import wx.domain.event.Event;
import wx.domain.event.EventHandler;
import wx.domain.event.EventHandlerRepository;

@Slf4j
@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMqEventBus.UFC_EVENT_BUS_QUEUE))
public class DomainEventHandler {

  private EventHandlerRepository eventHandlerRepository;

  public DomainEventHandler(EventHandlerRepository eventHandlerRepository) {
    this.eventHandlerRepository = eventHandlerRepository;
  }

  @RabbitHandler
  public void handle(Event event) {
    log.info("Handling {}", event);
    if (event != null) {
      handleEvent(event);
    } else {
      log.warn("Recv invalid domain event object: event == null");
    }
  }

  @SuppressWarnings("unchecked")
  private <E extends Event> void handleEvent(E event) {

    Optional<EventHandler<E>> handler =
        eventHandlerRepository.getEventHandler((Class<E>) event.getClass());

    if (handler.isPresent()) {
      try {
        handler.get().handle(event);
      } catch (Throwable t) {
        // TODO: 事件处理异常
        log.error("Error handling: " + event, t);
      }
    } else {
      log.warn("No handler for {}", event);
      throw new RuntimeException("No handler for " + event);
    }
  }
}

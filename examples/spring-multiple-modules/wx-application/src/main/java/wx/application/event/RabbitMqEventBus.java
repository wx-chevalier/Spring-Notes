package wx.application.event;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import wx.domain.event.Event;
import wx.domain.event.EventBus;

@Component
public class RabbitMqEventBus implements EventBus {

  public static final String UFC_EVENT_BUS_QUEUE = "UFC_EVENT_BUS";
  private AmqpTemplate amqpTemplate;

  public RabbitMqEventBus(AmqpTemplate amqpTemplate) {
    this.amqpTemplate = amqpTemplate;
  }

  @Override
  public void send(Event event) {
    this.amqpTemplate.convertAndSend(UFC_EVENT_BUS_QUEUE, event);
  }
}

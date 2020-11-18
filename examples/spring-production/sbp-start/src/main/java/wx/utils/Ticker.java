package wx.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import wx.context.properties.ApplicationProperty;

@Component
public class Ticker {
  private final TaskScheduler taskScheduler;
  private final Long monitorMeasurementIntervalMS;
  private final List<TickListener> listeners = new ArrayList<>();

  public Ticker(TaskScheduler taskScheduler, ApplicationProperty applicationProperty) {
    this.taskScheduler = taskScheduler;
    this.monitorMeasurementIntervalMS = applicationProperty.getMonitorMeasurementIntervalMS();
  }

  @PostConstruct
  public void init() {
    this.taskScheduler.scheduleAtFixedRate(
        this::execute, Duration.ofMillis(monitorMeasurementIntervalMS));
  }

  @PreDestroy
  public void destroy() {}

  private void execute() {
    for (TickListener listener : listeners) {
      listener.onTick();
    }
  }

  public void register(TickListener listener) {
    listeners.add(listener);
  }

  public void unregister(TickListener tickListener) {
    listeners.remove(tickListener);
  }

  public interface TickListener {
    void onTick();
  }
}

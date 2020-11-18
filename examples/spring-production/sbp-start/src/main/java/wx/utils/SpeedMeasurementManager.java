package wx.utils;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import wx.context.properties.ApplicationProperty;
import wx.domain.speed.SpeedMeasurement;

@Component
@Slf4j
public class SpeedMeasurementManager {
  private final TaskScheduler taskScheduler;
  private final Long monitorMeasurementIntervalMS;
  private final Clock clock;
  private final HashMap<String, SpeedMeasurement> speedMeasurementStore = new HashMap<>();
  private final HashMap<String, CurrentSpeed> currentSpeedStore = new HashMap<>();
  private final List<SpeedSource> speedSources = new ArrayList<>();

  public SpeedMeasurementManager(
      TaskScheduler taskScheduler, ApplicationProperty applicationProperty) {
    this.taskScheduler = taskScheduler;
    this.monitorMeasurementIntervalMS = applicationProperty.getMonitorMeasurementIntervalMS();
    this.clock = Clock.systemUTC();
  }

  @PostConstruct
  public void init() {
    this.taskScheduler.scheduleAtFixedRate(
        this::execute, Duration.ofMillis(monitorMeasurementIntervalMS));
  }

  @PreDestroy
  public void destroy() {
    speedSources.clear();
  }

  public void register(Collection<SpeedSource> sources) {
    //    log.debug("Registering {}", sources.stream().map(SpeedSource::getName).toArray());
    speedSources.addAll(sources);
  }

  public void register(SpeedSource speedSource) {
    //    log.debug("Registering {}", speedSource.getName());
    speedSources.add(speedSource);
  }

  public void unregister(SpeedSource speedSource) {
    speedSources.remove(speedSource);
  }

  public Optional<CurrentSpeed> getCurrentSpeedForName(String name) {
    return Optional.ofNullable(currentSpeedStore.get(name));
  }

  private void execute() {
    for (SpeedSource speedSource : speedSources) {
      SpeedMeasurement start = speedMeasurementStore.get(speedSource.getName());
      SpeedMeasurement end =
          new SpeedMeasurement(
              speedSource.getCurrentRead(),
              speedSource.getCurrentWrite(),
              LocalDateTime.now(clock));
      if (start != null) {
        final long readPerSecond =
            measureSpeed(start.getSampledAt(), end.getSampledAt(), start.getRead(), end.getRead());
        final long writePerSecond =
            measureSpeed(
                start.getSampledAt(), end.getSampledAt(), start.getWrite(), end.getWrite());

        //        log.trace(
        //            "Current speed for {}: read: {}/s write: {}/s",
        //            speedSource.getName(),
        //            readPerSecond,
        //            writePerSecond);

        currentSpeedStore.put(
            speedSource.getName(), new CurrentSpeed(readPerSecond, writePerSecond));
        speedMeasurementStore.put(speedSource.getName(), end);
      } else {
        //        log.debug("Initializing measurement for {}", speedSource.getName());
        speedMeasurementStore.put(speedSource.getName(), end);
      }
    }
  }

  private long measureSpeed(
      LocalDateTime start, LocalDateTime end, long valueStart, long valueEnd) {
    double duration = Duration.between(start, end).getSeconds();
    double deltaValue = (valueEnd - valueStart);
    if (deltaValue <= 0 || duration <= 0) {
      return 0L;
    }
    double valuePerSecond = deltaValue / duration;
    return (long) valuePerSecond;
  }

  public interface SpeedSource {
    String getName();

    long getCurrentRead();

    long getCurrentWrite();
  }

  public static class CurrentSpeed {
    private final long readPerSeconds;
    private final long writePerSeconds;

    CurrentSpeed(long readPerSeconds, long writePerSeconds) {
      this.readPerSeconds = readPerSeconds;
      this.writePerSeconds = writePerSeconds;
    }

    public long getReadPerSeconds() {
      return readPerSeconds;
    }

    public long getWritePerSeconds() {
      return writePerSeconds;
    }
  }
}

package wx.infra.common.util;

import static java.time.DayOfWeek.SUNDAY;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import wx.common.data.common.StatisticTimeUnit;

public class DateTimeUtils {

  // 获取一天中最小的时间，也就是当天的0点
  public static LocalDateTime getDayStartTime(LocalDateTime dateTime) {
    return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MIN);
  }

  // 获取一天中最大的时间，也就是当天的24点
  public static LocalDateTime getDayEndTime(LocalDateTime dateTime) {
    return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MAX);
  }

  /** 根据时间单位分割时间 */
  public static List<LocalDate> split(LocalDate start, LocalDate end, StatisticTimeUnit timeUnit) {
    switch (timeUnit) {
      case DAY:
        return splitWithDays(start, end);
      case WEEK:
        return splitWithWeek(start, end);
      case MONTH:
        return splitWithMonth(start, end);
      default:
        return null;
    }
  }

  /* 按周分割时间 */
  private static List<LocalDate> splitWithWeek(LocalDate start, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    dateList.add(start);
    while ((start = start.with(TemporalAdjusters.next(SUNDAY)).plusDays(1)).isBefore(end)) {
      dateList.add(start);
      start = start.plusDays(1);
    }
    dateList.add(end);
    return dateList;
  }

  /** 按月分割时间 */
  private static List<LocalDate> splitWithMonth(LocalDate start, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    dateList.add(start);
    while ((start = start.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)).isBefore(end)) {
      dateList.add(start);
      start = start.plusDays(1);
    }
    dateList.add(end);
    return dateList;
  }

  /** 按天分割时间 */
  private static List<LocalDate> splitWithDays(LocalDate start, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    LocalDate date = start;
    while (date.isBefore(end)) {
      dateList.add(date);
      date = date.plusDays(1);
    }
    dateList.add(end);
    return dateList;
  }

  // 获取当前时间格式 "2019-12-12 12:00:00"
  public static String currentDatetime() {
    return LocalDateTime.now()
        .atZone(ZoneOffset.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  /**
   * @param dateTime LocalDateTime
   * @return ms
   */
  public static long toTimestamp(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  /**
   * @param timestamp ms
   * @return LocalDateTime
   */
  public static LocalDateTime fromTimestamp(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
  }
}

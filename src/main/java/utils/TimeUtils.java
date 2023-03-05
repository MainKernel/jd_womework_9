package utils;

import java.time.ZoneId;

public class TimeUtils {

  private TimeUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static ZoneId getTimeZone(String timeZone) {
    if ("".equals(timeZone) || timeZone == null) {
      return null;
    }

    ZoneId zoneId = null;
    timeZone = timeZone.replace(" ", "+");
    try {
      zoneId = ZoneId.of(timeZone);
    } catch (RuntimeException e) {
      throw new RuntimeException();
    }
    return zoneId;
  }

  public static boolean validateTimeZone(String timeZone) {
    if ("".equals(timeZone) || timeZone == null) {
      return false;
    }

    timeZone = timeZone.replace(" ", "+");
    try {
      ZoneId.of(timeZone);
    } catch (RuntimeException e) {
      return false;
    }
    return true;
  }
}

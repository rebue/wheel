package rebue.wheel.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class LocalDateUtils {

    /**
     * Date转换为LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(final Date date) {
        final Instant instant = date.toInstant();// An instantaneous point on the time-line.(时间线上的一个瞬时点。)
        final ZoneId  zoneId  = ZoneId.systemDefault();// A time-zone ID, such as {@code Europe/Paris}.(时区)
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * LocalDateTime转换为Date
     */
    public static Date localDateTime2Date(final LocalDateTime localDateTime) {
        final ZoneId        zoneId = ZoneId.systemDefault();
        final ZonedDateTime zdt    = localDateTime.atZone(zoneId);// Combines this date-time with a time-zone to create a ZonedDateTime.
        return Date.from(zdt.toInstant());

    }

    /**
     * 获取LocalDateTime的毫秒数
     */
    public static Long getMillis(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}

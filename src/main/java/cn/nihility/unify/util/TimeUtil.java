package cn.nihility.unify.util;

import java.time.*;

/**
 * 时间工具
 */
public class TimeUtil {

    /**
     * 根据 {@link LocalDateTime} 获取对应的毫秒数. 时区默认使用的是系统当前时区.
     */
    public static Long toMilli(LocalDateTime localDateTime) {
        return toMilli(localDateTime, ZoneId.systemDefault());
    }

    /**
     * 根据 {@link LocalDate} 获取对应 零点 的秒数. 时区默认使用的是系统当前时区.
     *
     * @param localDate
     * @return
     */
    public static Long toMilli(LocalDate localDate) {
        return toMilli(localDate, ZoneId.systemDefault());
    }

    /**
     * 根据 {@link LocalTime} 获取对应 当天零点 的秒数. 时区默认使用的是系统当前时区.
     */
    public static Long toMilli(LocalTime localTime) {
        return toMilli(localTime, ZoneId.systemDefault());
    }

    /**
     * 根据 {@link LocalDate} 和 {@link ZoneId} 时区, 获取指定时区 零点 对应的毫秒数.
     */
    public static Long toMilli(LocalDate localDate, ZoneId zoneId) {
        if (localDate == null || zoneId == null) {
            return null;
        }
        return toMilli(LocalDateTime.of(localDate, LocalTime.MIN), zoneId);
    }

    /**
     * 根据 {@link LocalTime} 和 {@link ZoneId} 时区, 获取指定时区 当天零点 对应的毫秒数.
     */
    public static Long toMilli(LocalTime localTime, ZoneId zoneId) {
        if (localTime == null || zoneId == null) {
            return null;
        }
        return toMilli(LocalDateTime.of(LocalDate.now(), localTime), zoneId);
    }

    /**
     * 根据 {@link LocalDateTime} 和 {@link ZoneId} 时区, 获取指定时区对应的毫秒数.
     *
     * @param zoneId 时区
     */
    public static Long toMilli(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null || zoneId == null) {
            return null;
        }
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * timestamp(时间戳) 转换为 {@link LocalDate}
     *
     * @param timestamp 时间戳
     */
    public static LocalDate toLocalDate(long timestamp) {
        return toLocalDateTime(timestamp).toLocalDate();
    }

    /**
     * timestamp(时间戳) 转换为 {@link LocalTime}
     *
     * @param timestamp 时间戳
     */
    public static LocalTime toLocalTime(long timestamp) {
        return toLocalDateTime(timestamp).toLocalTime();
    }

    /**
     * timestamp(时间戳) 转换为 {@link LocalDateTime}
     *
     * @param timestamp 时间戳
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofHours(8));
    }


}

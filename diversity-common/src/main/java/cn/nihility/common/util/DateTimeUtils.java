package cn.nihility.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author nihility
 * @date 2022/03/07 10:16
 */
public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_TIME =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    public static LocalDateTime localDateTimeParse(String source) {
        return StringUtils.isBlank(source) ? null : LocalDateTime.parse(source, DATE_TIME_FORMATTER);
    }

    public static LocalDate localDateParse(String source) {
        return StringUtils.isBlank(source) ? null : LocalDate.parse(source, DATE_FORMATTER);
    }

    public static Date simpleDateParse(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        Date result = null;
        try {
            result = SIMPLE_DATE_TIME.get().parse(source);
        } catch (ParseException e) {
            try {
                result = SIMPLE_DATE.get().parse(source);
            } catch (ParseException ignore) {

            } finally {
                SIMPLE_DATE.remove();
            }
        } finally {
            SIMPLE_DATE_TIME.remove();
        }

        return result;
    }

}

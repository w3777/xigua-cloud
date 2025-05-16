package com.xigua.common.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName DateUtil
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/14 20:31
 */
public class DateUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            return "";
        }
        return formatter.format(dateTime);
    }

    public static String formatDate(LocalDate date, DateTimeFormatter formatter) {
        if (date == null) {
            return "";
        }
        return formatter.format(date);
    }

}

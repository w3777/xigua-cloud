package com.xigua.common.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    /**
     * LocalDateTime 转 时间戳
     * @author wangjinfei
     * @date 2025/7/27 12:16
     * @param localDateTime
     * @return long
    */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

}

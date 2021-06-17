package vn.viettel.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {
    public static String createExcelFileName() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return date + ".xlsx";
    }

    public static String createXmlFileName() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return date + ".xml";
    }

    public static boolean stringNotNullOrEmpty(String value) {
        if (value == null || "".equals(value.trim()))
            return false;

        return true;
    }
}

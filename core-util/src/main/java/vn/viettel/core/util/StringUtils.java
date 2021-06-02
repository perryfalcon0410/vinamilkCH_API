package vn.viettel.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {
    public static String createExcelFileName() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return date + ".xlsx";
    }
}

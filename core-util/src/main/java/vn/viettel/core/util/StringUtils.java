package vn.viettel.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {
    public static String createExcelFileName() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return date + ".xlsx";
    }

    public static String createXmlFileName(String shopCode) {
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        fileName +="."+StringUtils.createRandomFourNumber();
        fileName +="_"+shopCode+".xml";
        return fileName;
    }

    public static boolean stringNotNullOrEmpty(String value) {
        if (value == null || "".equals(value.trim()))
            return false;

        return true;
    }

    public static boolean stringIsNullOrEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    public static String createRandomFourNumber() {
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999 + 1);
        return String.valueOf(randomNum);
    }

    public static String substringAfter(String image, String s) {
        return image;
    }

    public static String substringAfterLast(String url, String s) {
        return url;
    }

    public static boolean isNotBlank(String url) {
        return true;
    }

    public static String substringBetween(String image, String s, String s1) {
        return image;
    }
}

package vn.viettel.core.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class TimeList {
    /**
     * the distance between the minutes
     */
    private final static int HOUR_OF_DAY = 24;
    private final static int MINUTE_OF_HOUR = 60;
    private final static int MINUTE_DISTANCE = 15;
    private final static List<String> TIMES = getTimeList();

    /**
     * get list time
     *
     * @return
     */
    public static List<String> getTimeList() {
        List<String> times = new LinkedList<>();
        LocalTime localTime = LocalTime.of(0, 0);
        for (int i = 0; i < HOUR_OF_DAY * (MINUTE_OF_HOUR / MINUTE_DISTANCE); i++) {
            times.add(localTime.plusMinutes(MINUTE_DISTANCE * i).toString());
        }
        return times;
    }

    /**
     * check list time isTimeValid time
     *
     * @param str String time
     * @return Boolean
     */
    public static boolean isTimeValid(String str) {
        return TIMES.contains(str);
    }

    /**
     * check time valid in the list
     *
     * @param time
     * @return true if valid , false if not valid
     */
    public static boolean isTimeValid(LocalTime time) {
        String pattern = "HH:mm";
        String time_str = time.format(DateTimeFormatter.ofPattern(pattern));
        return TIMES.contains(time_str);
    }

    public static boolean contains(CharSequence cs) {
        return TIMES.contains(cs);
    }

    public static boolean containsNone(CharSequence cs) {
        return !TIMES.contains(cs);
    }

}

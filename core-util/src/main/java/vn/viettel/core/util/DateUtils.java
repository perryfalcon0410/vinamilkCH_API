package vn.viettel.core.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

public class DateUtils {

    private static Calendar c = Calendar.getInstance();
    private final static int HOUR_OF_DAY = 24;
    private final static int MINUTE_OF_HOUR = 60;
    private final static int SECOND_OF_MINUTE = 60;
    private final static int MILISECOND_OF_SECOND = 1000;
    private final static int HOUR_DEFAULT = 0;
    private final static int MINUTE_DEFAULT = 0;
    private final static int SECOND_DEFAULT = 0;

    public static Date parseToDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), HOUR_DEFAULT, MINUTE_DEFAULT, SECOND_DEFAULT);
        return c.getTime();
    }

    public static Date parseToDate(LocalDate date, LocalTime time) {
        if (date == null) {
            return null;
        }
        c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond());
        return c.getTime();
    }

    public static LocalDate parseToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
    }

    public static LocalTime parseToLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalTime.parse(new SimpleDateFormat("HH:mm").format(date));
    }

    /**
     * convert date to String date
     *
     * @param date
     * @return String date format yyyy-MM-dd
     */
    public static String parseToStringDate(Date date) {
        if (date == null) {
            return null;
        }
        c.setTime(date);
        String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH) + "";
        String month = c.get(Calendar.MONTH) + 1 < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1) + "";
        String year = c.get(Calendar.YEAR) + "";
        return year + "-" + month + "-" + day;
    }

    /**
     * convert date to String time
     *
     * @param date
     * @return String time format HH:MM
     */
    public static String parseToStringTime(Date date) {
        if (date == null) {
            return null;
        }
        c.setTime(date);
        String hour = c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY) + "";
        String minute = c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE) + "";
        return hour + ":" + minute;
    }

    /**
     * convert date to epochDay
     *
     * @param date
     * @return Epoch day
     */
    public static Long parseToEpochDay(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime() / (MILISECOND_OF_SECOND * SECOND_OF_MINUTE * MINUTE_OF_HOUR * HOUR_OF_DAY);
    }

    /**
     * get number of day after today
     *
     * @param date
     * @return number of days before today, -1 if > today
     */
    public static Long afterTodayNumberOfDays(Date date) {
        if (date == null) {
            return null;
        }
        Long days = parseToEpochDay(date) - parseToEpochDay(new Date());
        return days >= 0 ? days : Long.valueOf(-1);
    }

    /**
     * get number of day after today
     *
     * @param date
     * @return number of days before today, -1 if > today
     */
    public static Long afterTodayNumberOfDays(LocalDate date) {
        if (date == null) {
            return null;
        }
        Long days = date.toEpochDay() - LocalDate.now().toEpochDay();
        return days >= 0 ? days : Long.valueOf(-1);
    }

    /**
     * get number of day before today
     *
     * @param date
     * @return number of days before today, -1 if < today
     */
    public static Long beforeTodayNumberOfDays(Date date) {
        if (date == null) {
            return null;
        }
        Long days = parseToEpochDay(new Date()) - parseToEpochDay(date);
        return days >= 0 ? days : Long.valueOf(-1);
    }

    /**
     * get number of day before today
     *
     * @param date
     * @return number of days before today, -1 if < today
     */
    public static Long beforeTodayNumberOfDays(LocalDate date) {
        if (date == null) {
            return null;
        }
        Long days = LocalDate.now().toEpochDay() - date.toEpochDay();
        return days >= 0 ? days : Long.valueOf(-1);
    }

    /**
     * get first day of month
     *
     * @param date
     * @return LocalDate first date of month
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(1);
    }

    /**
     * get last date of month
     *
     * @param date
     * @return LocalDate last date of month
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(lastDayOfMonth());
    }

    /**
     * get first date of month
     *
     * @return LocalDate first date of current month
     */
    public static LocalDate getFirstDayOfCurrentMonth() {
        return getFirstDayOfMonth(LocalDate.now());
    }

    /**
     * get last date of current month
     *
     * @return LocalDate last date of current month
     */
    public static LocalDate getLastDayOfCurrentMonth() {
        return getLastDayOfMonth(LocalDate.now());
    }

    /**
     * get is today
     *
     * @param date
     * @return true if to day, false if not to day
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return LocalDate.now().equals(date);
    }

    /*convert */
    public static int convertToWeekday(LocalDate date) {
        int weekday = date.getDayOfWeek().getValue();
        if (weekday == 7) {
            weekday = 0;
        }
        return weekday;
    }

    /**
     * convert epoch second to LocalDate
     *
     * @param epochSecond
     * @return LocalDate
     * EX: 1538326800 -> 2018-10-01
     */
    public static LocalDate convertEpochSecondToLocalDate(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * convert epoch second to LocalDateTime
     *
     * @param epochSecond
     * @return LocalDateTime
     * EX: 1538326800 -> 2018-10-01 00:00:00
     */
    public static LocalDateTime convertEpochSecondToLocalDateTime(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * @param LocalDate
     * @return String yyyy-MM
     */
    public static String parseToYearMonthFormat(LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }

    /**
     * get epoch seconds first day of this month
     *
     * @return epoch seconds
     */
    public static long getEpochSecondFirstDayOfThisMonth() {
        return LocalDateTime.now()
            .with(firstDayOfMonth())
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond();
    }

    /**
     * get epoch seconds last day of this month
     *
     * @return epoch seconds
     */
    public static long getEpochSecondLastDayOfThisMonth() {
        return LocalDateTime.now()
            .with(lastDayOfMonth())
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond();
    }

    public static Float convertLocalTimeToHourFloat(LocalTime time) {
        int hours = time.getHour();
        int minutes = time.getMinute();
        return hours + Float.valueOf(minutes / MINUTE_OF_HOUR);
    }
    
    
    public static String convertJapanStringTime(LocalDate date, Date time) {
    	DateTimeFormatter pattern = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.JAPAN);
    	String dateStr = date.format(pattern);
    	String timeStr = DateUtils.parseToStringTime(time);
    	
    	return dateStr.concat(" ").concat(timeStr);
    }
    
    public static String convertJapanStringTime(LocalDateTime date) {
    	DateTimeFormatter pattern = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.JAPAN);
    	String dateStr = date.format(pattern);
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    	String timeStr = date.format(formatter);
    	
    	return dateStr.concat(" ").concat(timeStr);
    }


}

package vn.viettel.core.util;

import java.text.DateFormat;
import java.text.ParseException;
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
    public static LocalDateTime convertFromDate(Date sFromDate)
    {
        if( sFromDate == null) return null;
        LocalDateTime localDateTime = LocalDateTime.of(sFromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MIN);
        return localDateTime;
    }

    public static LocalDateTime convertToDate(Date sToDate)
    {
        if(sToDate == null) return null;
        LocalDateTime localDateTime = LocalDateTime
                .of(sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        return localDateTime;
    }

    public static LocalDate convert2Local(Date date)
    {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime convertFromDate(LocalDate localDate)
    {
        if( localDate == null) return null;
        return localDate.atTime(LocalTime.MIN);
    }

    public static LocalDateTime convertToDate( LocalDate localDate)
    {
        if(localDate == null) return null;
        return localDate.atTime(LocalTime.MAX);
    }

    public static LocalDateTime convertFromDate(LocalDateTime localDate)
    {
        if( localDate == null) return null;
        return localDate.toLocalDate().atTime(LocalTime.MIN);
    }

    public static LocalDateTime convertToDate( LocalDateTime localDate)
    {
        if(localDate == null) return null;
        return localDate.toLocalDate().atTime(LocalTime.MAX);
    }

    public static LocalDateTime getFromDate(LocalDate localDate)
    {
        if( localDate == null) return null;
        return localDate.atTime(LocalTime.MIN);
    }

    public static LocalDateTime getToDate( LocalDate localDate)
    {
        if(localDate == null) return null;
        return localDate.atTime(LocalTime.MAX);
    }

    public static Date parseToDate(LocalDate date, LocalTime time) {
        Calendar c = Calendar.getInstance();
        if (date == null) {
            return null;
        }
        c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond());
        return c.getTime();
    }

    public static LocalDateTime convertDateToLocalDateTime(Date Date)
    {
        if(Date == null) return null;
        return LocalDateTime.ofInstant(Date.toInstant(), ZoneId.systemDefault());
    }
    /**
     * get first day of month
     *
     * @param date
     * @return LocalDate first date of month
     */
    public static LocalDateTime getFirstDayOfMonth(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return convertFromDate(date.withDayOfMonth(1).toLocalDate());
    }

    /**
     * get last date of month
     *
     * @param date
     * @return LocalDate last date of month
     */
    public static LocalDateTime getLastDayOfMonth(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return convertToDate(date.with(lastDayOfMonth()).toLocalDate());
    }

    /**
     * get first date of month
     *
     * @return LocalDate first date of current month
     */
    public static LocalDateTime getFirstDayOfCurrentMonth() {
        return getFirstDayOfMonth(LocalDateTime.now());
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

    public static String formatDate2StringDate(LocalDateTime dateTime) {
        if (dateTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(dateTime);
    }

    public static String formatDate2StringDate(LocalDate dateTime) {
        if (dateTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(dateTime);
    }

    public static String formatDate2StringDate(Date date) {
        if (date == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
    }

    public static String formatDate2StringDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss");
        return formatter.format(dateTime);
    }
    /**
     * convert String to LocalDateTime
     */
    public static LocalDateTime convertStringToLocalDateTime(String date) {
        if (date == null)
            return LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime;
    }

    public static LocalDateTime forMatDateObject(Object date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            java.util.Date newDate = formatter.parse(date.toString());
            LocalDateTime localDateTime = DateUtils.convertDateToLocalDateTime(newDate);
            return localDateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  static LocalDateTime min = DateUtils.convertStringToLocalDateTime("0001-01-01 00:00:00");
    public  static LocalDateTime max = DateUtils.convertStringToLocalDateTime("9999-12-21 23:59:59");
}

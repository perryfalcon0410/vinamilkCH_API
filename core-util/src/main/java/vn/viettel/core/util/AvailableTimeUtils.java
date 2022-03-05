//package vn.viettel.core.util;
//
//import java.time.LocalTime;
//
//public final class AvailableTimeUtils {
//
//    private static final int NUMBER_OF_MINUTES_IN_ONE_UNIT = 15;
//
//    private static final int NUMBER_OF_MINUTES_IN_ONE_HOUR = 60;
//    private static final int NUMBER_OF_HOURS_IN_ONE_DAY = 24;
//
//    private static final int NUMBER_OF_UNIT_IN_ONE_HOUR = NUMBER_OF_MINUTES_IN_ONE_HOUR / NUMBER_OF_MINUTES_IN_ONE_UNIT;
//    private static final int NUMBER_OF_UNIT_IN_ONE_DAY = NUMBER_OF_UNIT_IN_ONE_HOUR * NUMBER_OF_HOURS_IN_ONE_DAY;
//
//    private final static String UNIT_TIME_OPEN = "1";
//    private final static String UNIT_TIME_CLOSE = "0";
//
//    public static String generateAvailableTime(String openTimeString, String closeTimeString, String breakTimeStartString, String breakTimeEndString) {
//
//        int numberOfOpenTimeUnit = convertTimeStringToNumberOfAvailableTimeUnit(openTimeString);
//        int numberOfCloseTimeUnit = convertTimeStringToNumberOfAvailableTimeUnit(closeTimeString);
//        int numberOfBreakTimeStartUnit = convertTimeStringToNumberOfAvailableTimeUnit(breakTimeStartString);
//        int numberOfBreakTimeEndUnit = convertTimeStringToNumberOfAvailableTimeUnit(breakTimeEndString);
//
//        int numberOfCharactersOfSegment1 = numberOfOpenTimeUnit;
//        int numberOfCharactersOfSegment2 = numberOfBreakTimeStartUnit - numberOfOpenTimeUnit;
//        int numberOfCharactersOfSegment3 = numberOfBreakTimeEndUnit - numberOfBreakTimeStartUnit;
//        int numberOfCharactersOfSegment4 = numberOfCloseTimeUnit - numberOfBreakTimeEndUnit;
//        int numberOfCharactersOfSegment5 = NUMBER_OF_UNIT_IN_ONE_DAY - numberOfCloseTimeUnit;
//
//        return new StringBuilder()
//            .append(StringUtils.repeat(UNIT_TIME_CLOSE, numberOfCharactersOfSegment1))
//            .append(StringUtils.repeat(UNIT_TIME_OPEN, numberOfCharactersOfSegment2))
//            .append(StringUtils.repeat(UNIT_TIME_CLOSE, numberOfCharactersOfSegment3))
//            .append(StringUtils.repeat(UNIT_TIME_OPEN, numberOfCharactersOfSegment4))
//            .append(StringUtils.repeat(UNIT_TIME_CLOSE, numberOfCharactersOfSegment5))
//            .toString();
//    }
//
//    private static int convertTimeStringToNumberOfAvailableTimeUnit(String timeString) {
//        LocalTime time = LocalTime.parse(timeString);
//
//        int hour = time.getHour();
//        int minute = time.getMinute();
//
//        return (hour * NUMBER_OF_UNIT_IN_ONE_HOUR) + (minute / NUMBER_OF_MINUTES_IN_ONE_UNIT);
//    }
//
//}

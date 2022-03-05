package vn.viettel.core.util;

import java.util.function.Predicate;

public class ValidationUtils {

//    public static <T extends Enum<T> & Validatable> boolean isValidEnumValue(Class<T> enumClazz, Object value) {
//        List<T> avaiableEnums = EnumUtils.getEnumList(enumClazz);
//        return avaiableEnums.stream().anyMatch(anEnum -> anEnum.validateValue().equals(String.valueOf(value)));
//    }

    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final Predicate<String> MORE_THAN_SIX_CHARS = s -> s.length() >= 6;
    private static final Predicate<String> NO_SPACE = s -> s.indexOf(' ') == -1;
    private static final Predicate<String> CONTAINS_ONE_DIGIT = s -> s.chars().anyMatch(Character::isDigit);
    private static final Predicate<String> CONTAINS_ONE_SPECIAL_CHAR = s -> s.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) > -1);
    private static final Predicate<String> CONTAINS_LOWER_CASE_CHAR = s -> s.chars().anyMatch(Character::isLowerCase);
    private static final Predicate<String> CONTAINS_UPPER_CASE_CHAR = s -> s.chars().anyMatch(Character::isUpperCase);

    public static boolean checkPassword(String password) {
        return MORE_THAN_SIX_CHARS
                .and(NO_SPACE)
                .and(CONTAINS_ONE_DIGIT)
                .and(CONTAINS_ONE_SPECIAL_CHAR)
                .and(CONTAINS_LOWER_CASE_CHAR.or(CONTAINS_UPPER_CASE_CHAR))
                .test(password);
    }

    public static boolean equalDouble(double x, double y) {
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(y);
    }

}

package vn.viettel.core.util;

import java.util.function.Predicate;

public class ValidationUtils {

    private static final String SPECIAL_CHARS = "!@#$%^&*";

    public static boolean equalDouble(double x, double y) {
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(y);
    }

}

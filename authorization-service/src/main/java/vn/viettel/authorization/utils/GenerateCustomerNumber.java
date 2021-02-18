package vn.viettel.authorization.utils;

import org.apache.commons.lang3.StringUtils;

public class GenerateCustomerNumber {

    private final static int CODE_LENGTH = 10;

    public static String create(Long id) {
        int remainingLength = CODE_LENGTH - id.toString().length();
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.repeat("0", remainingLength));
        sb.append(id);
        return sb.toString();
    }
}

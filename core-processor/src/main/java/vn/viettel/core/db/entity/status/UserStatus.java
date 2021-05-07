package vn.viettel.core.db.entity.status;

import vn.viettel.core.util.status.Validatable;

import java.util.HashMap;
import java.util.Map;

public enum UserStatus implements Validatable {

    ACTIVE(1, "Active"),
    BANNED(2, "Banned"),
    UNACTIVE(0, "Unactive");

    private final int code;
    private String status;

    private static final Map<Integer, UserStatus> codeValues = new HashMap<>();

    static {
        for (UserStatus us : UserStatus.values()) {
            codeValues.put(us.code, us);
        }
    }

    UserStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public String status() {
        return this.status;
    }

    public String value() {
        return this.status;
    }

    public static String getStatus(int code) {
        return codeValues.get(code) != null ? codeValues.get(code).status : "";
    }

    @Override
    public String validateValue() {
        return this.status;
    }
}

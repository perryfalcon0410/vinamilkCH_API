package vn.viettel.core.db.entity.enums.customer;

public enum Gender {
    MALE(0, "Male"),
    FEMALE(1, "Female"),
    OTHER(2, "other"),
    ;
    private final int code;
    private final String gender;

    private Gender(int code, String gender) {
        this.code = code;
        this.gender = gender;
    }

    public int getCode() {return code;}
    public String getGender() {return gender;}
}

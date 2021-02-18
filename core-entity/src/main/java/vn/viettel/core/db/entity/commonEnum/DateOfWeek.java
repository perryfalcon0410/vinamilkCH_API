package vn.viettel.core.db.entity.commonEnum;

import java.time.DayOfWeek;

public enum DateOfWeek {
    MONDAY(1, "MONDAY"),
    TUESDAY(2, "TUESDAY"),
    WEDNESDAY(3, "WEDNESDAY"),
    THURSDAY(4, "THURSDAY"),
    FRIDAY(5, "FRIDAY"),
    SATURDAY(6, "SATURDAY"),
    SUNDAY(7, "SUNDAY");

    private long id;

    private String date;

    DateOfWeek(long id, String date) {
        this.id = id;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public static DateOfWeek getDateOfWeekByName(DayOfWeek dayValue) {
        for(DateOfWeek e : DateOfWeek.values()) {
            if(e.getDate().contentEquals(dayValue.name())) return e;
        }
        return null;
    }

}

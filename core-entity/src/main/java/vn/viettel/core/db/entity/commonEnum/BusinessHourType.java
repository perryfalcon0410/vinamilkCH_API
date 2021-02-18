package vn.viettel.core.db.entity.commonEnum;

public enum BusinessHourType {
    OPEN_ON_MONDAY_MORNING(1, "Open on Monday Morning"),
    OPEN_ON_MONDAY_AFTERNOON(2, "Open on Monday Afternoon"),
    OPEN_ON_TUESDAY_MORNING(3, "Open on Tuesday Morning"),
    OPEN_ON_TUESDAY_AFTERNOON(4, "Open on Tuesday Afternoon"),
    OPEN_ON_WEDNESDAY_MORNING(5, "Open on Wednesday Morning"),
    OPEN_ON_WEDNESDAY_AFTERNOON(6, "Open on Wednesday Afternoon"),
    OPEN_ON_THURSDAY_MORNING(7, "Open on Thursday Morning"),
    OPEN_ON_THURSDAY_AFTERNOON(8, "Open on Thursday Afternoon"),
    OPEN_ON_FRIDAY_MORNING(9, "Open on Friday Morning"),
    OPEN_ON_FRIDAY_AFTERNOON(10, "Open on Friday Afternoon"),
    OPEN_ON_SATURDAY_MORNING(11, "Open on Saturday Morning"),
    OPEN_ON_SATURDAY_AFTERNOON(12, "Open on Saturday Afternoon"),
    OPEN_ON_SUNDAY_MORNING(13, "Open on Sunday Morning"),
    OPEN_ON_SUNDAY_AFTERNOON(14, "Open on Sunday Afternoon"),
    OFF_SPECIAL_DAY(15, "DAY OFF (Holiday)"),
    OFF_URGENT_DAY(16, "DAY OFF (Urgent)");

    private long id;

    private String businessHourTypeName;

    BusinessHourType(long id, String businessHourTypeName) {
        this.id = id;
        this.businessHourTypeName = businessHourTypeName;
    }

    public long getId() {
        return id;
    }

    public String getBusinessHourTypeName() {
        return businessHourTypeName;
    }

    public static DateOfWeek getDateOfWeek(long idDate) {
        for(DateOfWeek e : DateOfWeek.values()) {
            if(e.getId() == (((idDate-1) / 2) + 1)) return e;
        }
        return null;
    }

    public static boolean isMorning(long businessHourTypeId){
        if(businessHourTypeId % 2 == 0){
            return false;
        }
        return true;
    }

    public static Long getIdIsMorningByDayName(String dayName){
        for(BusinessHourType item : BusinessHourType.values()){
            if(item.getBusinessHourTypeName().toUpperCase().contains(dayName) && item.getId()%2!=0){
                return item.getId();
            }
        }
        return null;
    }

    public static Long getIdIsAfternoonByDayName(String dayName){
        for(BusinessHourType item : BusinessHourType.values()){
            if(item.getBusinessHourTypeName().toUpperCase().contains(dayName) && item.getId()%2==0){
                return item.getId();
            }
        }
        return null;
    }

    public static Long getMorningId(DateOfWeek date) {
        return date.getId()*2-1;
    }

    public static Long getAfternoonId(DateOfWeek date) {
        return date.getId()*2;
    }
}

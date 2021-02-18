package vn.viettel.core.db.entity.commonEnum;

public enum WorkingStatus {
    SAME_AS_SALON(1, "Same as Salon", "Same as Salon"),
    SPECIFIC_DATE_AND_TIME(2, "Specific Date and Time", "Specific Date And Time"),
    ABSENT(3, "Absent", "Absent in that day");

    private long id;

    private String name;

    private String description;

    WorkingStatus(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static WorkingStatus getWorkingStatusById(Long id) {
        for (WorkingStatus e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}

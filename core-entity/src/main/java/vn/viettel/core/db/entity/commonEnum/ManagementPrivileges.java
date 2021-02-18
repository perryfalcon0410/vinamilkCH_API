package vn.viettel.core.db.entity.commonEnum;

public enum ManagementPrivileges {
    HEAD_QUARTER_ADMIN(1, "Head quarter Admin"),
    COMPANY_HEAD_QUARTER_MANAGER(2, "Company(Head quarter) Manager"),
    COMPANY_HEAD_QUARTER_STAFF(3, "Company(Head quarter) Staff"),
    AREA_DIRECTOR(4, "Area Director"),
    AREA_MANAGER(5, "Area Manager"),
    SALON_MANAGER(6, "Salon Manager"),
    SALON_CHIEF(7, "Salon Chief"),
    SALON_SUB_CHIEF(8, "Salon Sub Chief"),
    SALON_SUB(9, "Salon Sub"),
    SALON_BEAUTICIAN_EMPLOYEE(10, "Salon Beautician Employee"),
    SALON_BEAUTICIAN_PART_TIME(11, "Salon Beautician Parttime"),
    ALL_SALON_MEMBERS(12, "All Salon Members"),
    RECEPTION(13, "Reception");

    private long id;

    private String privilegeName;

    ManagementPrivileges(long id, String privilegeName) {
        this.id = id;
        this.privilegeName = privilegeName;
    }

    public long getId() {
        return id;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }
}

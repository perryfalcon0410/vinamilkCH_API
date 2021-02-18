package vn.viettel.core.db.entity.commonEnum;

public enum BookingPhotoType {
    HORIZONTAL(1, "よこ", "Horizontal"),
    BEHIND(2, "うしろ", "Behind"),
    FONT(3, "まえ", "Front");

    private long id;
    private String jpName;
    private String enName;

    BookingPhotoType(long id, String jpName, String enName) {
        this.id = id;
        this.jpName = jpName;
        this.enName = enName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJpName() {
        return jpName;
    }

    public void setJpName(String jpName) {
        this.jpName = jpName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public static Long getIdByName(String enName) {
        for (BookingPhotoType e : BookingPhotoType.values()) {
            if (e.getEnName().toUpperCase().equals(enName)) {
                return e.getId();
            }
        }
        return null;
    }

    public static String getJpNameByEnName(String enName) {
        for (BookingPhotoType e : BookingPhotoType.values()) {
            if (e.getEnName().toUpperCase().equals(enName)) {
                return e.getJpName();
            }
        }
        return null;
    }
}

package vn.viettel.core.db.entity.commonEnum;

public enum CouponType {
    COUPON_PRIVATE(1L, "マイクーポン","my coupon"),
    COUPON_ADMIN_PUBLIC(2L, "公開クーポン","public coupon");

    private long id;

    private String tag;

    private String enName;

    CouponType(long id, String tag, String enName) {
        this.id = id;
        this.tag = tag;
        this.enName = enName;
    }

    public long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getEnName() {
        return enName;
    }

    public CouponType getCouponTypeById() {
        for (CouponType e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }

    public static Long getIdByEnName(String enName) {
        for (CouponType e : CouponType.values()) {
            if (enName.toLowerCase().equals(e.getEnName())) {
                return e.getId();
            }
        }
        return null;
    }

    public static String getNameById(Long id){
        for (CouponType e : CouponType.values()){
            if(id == e.getId()){
                return e.getEnName();
            }
        }
        return null;
    }
}

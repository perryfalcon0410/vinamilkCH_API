package vn.viettel.core.db.entity.commonEnum;

public enum CouponDiscountType {
    DISCOUNT_PRICE(1L, "値引き","direct"),
    DISCOUNT_PERCENT(2L, "割引", "factor"),
    DISCOUNT_PRODUCT(3L, "商品","product");
    private Long id;
    private String jpName;
    private String enName;

    CouponDiscountType(Long id, String jpName, String enName) {
        this.id = id;
        this.jpName = jpName;
        this.enName = enName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public static Byte getIdByName(String enName) {
        for (CouponDiscountType e : CouponDiscountType.values()) {
            if (enName.toLowerCase().equals(e.getEnName())) {
                return Byte.parseByte(String.valueOf(e.getId()));
            }
        }
        return null;
    }
}

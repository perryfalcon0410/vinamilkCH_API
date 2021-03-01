package vn.viettel.core.dto;

public class SiteMapDTO {

    private Long id;

    private String topPage;

    private String eventPage;

    private String pointCouponPage;

    private String reservationPage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopPage() {
        return topPage;
    }

    public void setTopPage(String topPage) {
        this.topPage = topPage;
    }

    public String getEventPage() {
        return eventPage;
    }

    public void setEventPage(String eventPage) {
        this.eventPage = eventPage;
    }

    public String getPointCouponPage() {
        return pointCouponPage;
    }

    public void setPointCouponPage(String pointCouponPage) {
        this.pointCouponPage = pointCouponPage;
    }

    public String getReservationPage() {
        return reservationPage;
    }

    public void setReservationPage(String reservationPage) {
        this.reservationPage = reservationPage;
    }
}

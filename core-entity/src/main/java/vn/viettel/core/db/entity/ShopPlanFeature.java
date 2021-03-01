package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_plan_features")
public class ShopPlanFeature extends BaseEntity {

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "max_visitor_per_month")
    private Long maxVisitorPerMonth;

    @Column(name = "is_reservation")
    private boolean isReservation;

    @Column(name = "is_notification")
    private boolean isNotification;

    @Column(name = "is_inquiry")
    private boolean isInquiry;

    @Column(name = "is_event_reservation")
    private boolean isEventReservation;

    @Column(name = "is_point")
    private boolean isPoint;

    @Column(name = "is_message")
    private boolean isMessage;

    @Column(name = "max_item_create")
    private Long maxItemCreate;

    @Column(name = "max_menu_create")
    private Long maxMenuCreate;

    @Column(name = "max_notification_create")
    private Long maxNotificationCreate;

    @Column(name = "max_point_coupon_create")
    private Long maxPointCouponCreate;

    @Column(name = "max_message_create")
    private Long maxMessageCreate;

    @Column(name = "reservation_online_booking")
    private Boolean reservationOnlineBooking;

    @Column(name = "reservation_manual_registration")
    private Boolean reservationManualRegistration;

    @Column(name = "reservation_share_the_menu")
    private Boolean reservationShareTheMenu;

    @Column(name = "reservation_credit_card")
    private Boolean reservationCreditCard;

    @Column(name = "max_event_reservation_create")
    private Long maxEventReservationCreate;

    @Column(name = "max_program_reservation_create")
    private Long maxProgramReservationCreate;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getMaxVisitorPerMonth() {
        return maxVisitorPerMonth;
    }

    public void setMaxVisitorPerMonth(Long maxVisitorPerMonth) {
        this.maxVisitorPerMonth = maxVisitorPerMonth;
    }

    public boolean isReservation() {
        return isReservation;
    }

    public void setReservation(boolean reservation) {
        isReservation = reservation;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public boolean isInquiry() {
        return isInquiry;
    }

    public void setInquiry(boolean inquiry) {
        isInquiry = inquiry;
    }

    public boolean isEventReservation() {
        return isEventReservation;
    }

    public void setEventReservation(boolean eventReservation) {
        isEventReservation = eventReservation;
    }

    public boolean isPoint() {
        return isPoint;
    }

    public void setPoint(boolean point) {
        isPoint = point;
    }

    public boolean isMessage() {
        return isMessage;
    }

    public void setMessage(boolean message) {
        isMessage = message;
    }

    public Long getMaxItemCreate() {
        return maxItemCreate;
    }

    public void setMaxItemCreate(Long maxItemCreate) {
        this.maxItemCreate = maxItemCreate;
    }

    public Long getMaxMenuCreate() {
        return maxMenuCreate;
    }

    public void setMaxMenuCreate(Long maxMenuCreate) {
        this.maxMenuCreate = maxMenuCreate;
    }

    public Long getMaxNotificationCreate() {
        return maxNotificationCreate;
    }

    public void setMaxNotificationCreate(Long maxNotificationCreate) {
        this.maxNotificationCreate = maxNotificationCreate;
    }

    public Long getMaxPointCouponCreate() {
        return maxPointCouponCreate;
    }

    public void setMaxPointCouponCreate(Long maxPointCouponCreate) {
        this.maxPointCouponCreate = maxPointCouponCreate;
    }

    public Long getMaxMessageCreate() {
        return maxMessageCreate;
    }

    public void setMaxMessageCreate(Long maxMessageCreate) {
        this.maxMessageCreate = maxMessageCreate;
    }

    public Boolean getReservationOnlineBooking() {
        return reservationOnlineBooking;
    }

    public void setReservationOnlineBooking(Boolean reservationOnlineBooking) {
        this.reservationOnlineBooking = reservationOnlineBooking;
    }

    public Boolean getReservationManualRegistration() {
        return reservationManualRegistration;
    }

    public void setReservationManualRegistration(Boolean reservationManualRegistration) {
        this.reservationManualRegistration = reservationManualRegistration;
    }

    public Boolean getReservationShareTheMenu() {
        return reservationShareTheMenu;
    }

    public void setReservationShareTheMenu(Boolean reservationShareTheMenu) {
        this.reservationShareTheMenu = reservationShareTheMenu;
    }

    public Boolean getReservationCreditCard() {
        return reservationCreditCard;
    }

    public void setReservationCreditCard(Boolean reservationCreditCard) {
        this.reservationCreditCard = reservationCreditCard;
    }

    public Long getMaxEventReservationCreate() {
        return maxEventReservationCreate;
    }

    public void setMaxEventReservationCreate(Long maxEventReservationCreate) {
        this.maxEventReservationCreate = maxEventReservationCreate;
    }

    public Long getMaxProgramReservationCreate() {
        return maxProgramReservationCreate;
    }

    public void setMaxProgramReservationCreate(Long maxProgramReservationCreate) {
        this.maxProgramReservationCreate = maxProgramReservationCreate;
    }
}

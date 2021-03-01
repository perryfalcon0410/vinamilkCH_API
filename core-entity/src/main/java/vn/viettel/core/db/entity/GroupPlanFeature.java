package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "group_plan_features")
public class GroupPlanFeature extends BaseEntity {

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "max_visitor_per_month")
    private Long maxVisitorPerMonth;

    @Column(name = "is_affiliated_shop")
    private boolean isAffiliated;

    @Column(name = "is_reservation")
    private boolean isReservation;

    @Column(name = "is_notification")
    private boolean isNotification;

    @Column(name = "is_inquiry")
    private boolean isInquiry;

    @Column(name = "is_event_reservation")
    private boolean isEventReservation;

    @Column(name = "is_point_coupon")
    private boolean isPointCoupon;

    @Column(name = "is_message")
    private boolean isMessage;

    @Column(name = "max_affiliated_shop")
    private Long maxAffiliatedShop;

    @Column(name = "max_notification_create")
    private Long maxNotificationCreate;

    @Column(name = "max_event_reservation_create")
    private Long maxEventReservationCreate;

    @Column(name = "max_program_reservation_create")
    private Long maxProgramReservationCreate;

    @Column(name = "max_point_coupon_create")
    private Long maxPointCouponCreate;

    @Column(name = "max_message_create")
    private Long maxMessageCreate;

    @Column(name = "reservation_online_booking")
    private boolean reservationOnlineBooking;

    @Column(name = "reservation_manual_registration")
    private boolean reservationManualRegistration;

    @Column(name = "reservation_share_the_menu")
    private boolean reservationShareTheMenu;

    @Column(name = "reservation_credit_card")
    private boolean reservationCreditCard;

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

    public boolean isAffiliated() {
        return isAffiliated;
    }

    public void setAffiliated(boolean affiliated) {
        isAffiliated = affiliated;
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

    public boolean isPointCoupon() {
        return isPointCoupon;
    }

    public void setPointCoupon(boolean pointCoupon) {
        isPointCoupon = pointCoupon;
    }

    public boolean isMessage() {
        return isMessage;
    }

    public void setMessage(boolean message) {
        isMessage = message;
    }

    public Long getMaxAffiliatedShop() {
        return maxAffiliatedShop;
    }

    public void setMaxAffiliatedShop(Long maxAffiliatedShop) {
        this.maxAffiliatedShop = maxAffiliatedShop;
    }

    public Long getMaxNotificationCreate() {
        return maxNotificationCreate;
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

    public Long getMaxPointCouponCreate() {
        return maxPointCouponCreate;
    }

    public void setMaxPointCouponCreate(Long maxPointCouponCreate) {
        this.maxPointCouponCreate = maxPointCouponCreate;
    }

    public void setMaxNotificationCreate(Long maxNotificationCreate) {
        this.maxNotificationCreate = maxNotificationCreate;
    }

    public Long getMaxMessageCreate() {
        return maxMessageCreate;
    }

    public void setMaxMessageCreate(Long maxMessageCreate) {
        this.maxMessageCreate = maxMessageCreate;
    }

    public boolean isReservationOnlineBooking() {
        return reservationOnlineBooking;
    }

    public void setReservationOnlineBooking(boolean reservationOnlineBooking) {
        this.reservationOnlineBooking = reservationOnlineBooking;
    }

    public boolean isReservationManualRegistration() {
        return reservationManualRegistration;
    }

    public void setReservationManualRegistration(boolean reservationManualRegistration) {
        this.reservationManualRegistration = reservationManualRegistration;
    }

    public boolean isReservationShareTheMenu() {
        return reservationShareTheMenu;
    }

    public void setReservationShareTheMenu(boolean reservationShareTheMenu) {
        this.reservationShareTheMenu = reservationShareTheMenu;
    }

    public boolean isReservationCreditCard() {
        return reservationCreditCard;
    }

    public void setReservationCreditCard(boolean reservationCreditCard) {
        this.reservationCreditCard = reservationCreditCard;
    }
}

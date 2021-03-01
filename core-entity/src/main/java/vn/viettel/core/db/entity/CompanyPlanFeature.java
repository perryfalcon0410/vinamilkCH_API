package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "company_plan_features")
public class CompanyPlanFeature extends BaseEntity {

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "max_visitor_per_month")
    private Long maxVisitorPerMonth;

    @Column(name = "is_product_solution")
    private boolean isProductSolution;

    @Column(name = "is_recruitment")
    private boolean isRecruitment;

    @Column(name = "is_reservation")
    private boolean isReservation;

    @Column(name = "is_notification")
    private boolean isNotification;

    @Column(name = "is_inquiry")
    private boolean isInquiry;

    @Column(name = "max_notification_create")
    private Long maxNotificationCreate;

    @Column(name = "max_event_reservation_create")
    private Long maxEventReservationCreate;

    @Column(name = "max_program_reservation_create")
    private Long maxProgramReservationCreate;

    @Column(name = "max_product_solution_create")
    private Long maxProductSolutionCreate;

    @Column(name = "max_recruitment_create")
    private Long maxRecruitmentCreate;

    @Column(name = "reservation_online_booking")
    private boolean reservationOnlineBooking;

    @Column(name = "reservation_manual_registration")
    private boolean reservationManualRegistration;

    @Column(name = "reservation_share_the_menu")
    private boolean reservationShareTheMenu;

    @Column(name = "reservation_credit_card")
    private boolean reservationCreditCard;

    public boolean isProductSolution() {
        return isProductSolution;
    }

    public void setProductSolution(boolean productSolution) {
        isProductSolution = productSolution;
    }

    public boolean isRecruitment() {
        return isRecruitment;
    }

    public void setRecruitment(boolean recruitment) {
        isRecruitment = recruitment;
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

    public Long getMaxProductSolutionCreate() {
        return maxProductSolutionCreate;
    }

    public void setMaxProductSolutionCreate(Long maxProductSolutionCreate) {
        this.maxProductSolutionCreate = maxProductSolutionCreate;
    }

    public Long getMaxRecruitmentCreate() {
        return maxRecruitmentCreate;
    }

    public void setMaxRecruitmentCreate(Long maxRecruitmentCreate) {
        this.maxRecruitmentCreate = maxRecruitmentCreate;
    }

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

    public Long getMaxNotificationCreate() {
        return maxNotificationCreate;
    }

    public void setMaxNotificationCreate(Long maxNotificationCreate) {
        this.maxNotificationCreate = maxNotificationCreate;
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

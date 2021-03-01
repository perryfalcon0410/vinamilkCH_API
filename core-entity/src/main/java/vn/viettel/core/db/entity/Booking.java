package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@AttributeOverride(name = "id", column = @Column(name = "booking_id"))
public class Booking extends BaseEntity {

    @Column(name = "reception_id", nullable = false)
    private Long receptionId;

    @Column(name = "booking_code")
    private String bookingCode;

    @Column(name = "start_schedule_date")
    private LocalDateTime startScheduleDate;

    @Column(name = "end_schedule_date")
    private LocalDateTime endScheduleDate;

    @Column(name = "start_treatment_schedule")
    private LocalDateTime startTreatmentSchedule;

    @Column(name = "end_treatment_schedule")
    private LocalDateTime endTreatmentSchedule;

    @Column(name = "status")
    private Long status;

    @Column(name = "point")
    private Double point;

    @Column(name = "finish_schedule_date")
    private LocalDateTime finishScheduleDate;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "memo")
    private String memo;

    @Column(name = "candidate_first_month")
    private LocalDateTime candidateFirstMonth;

    @Column(name = "candidate_second_month")
    private LocalDateTime candidateSecondMonth;

    @Column(name = "candidate_third_month")
    private LocalDateTime candidateThirdMonth;

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public LocalDateTime getStartScheduleDate() {
        return startScheduleDate;
    }

    public void setStartScheduleDate(LocalDateTime startScheduleDate) {
        this.startScheduleDate = startScheduleDate;
    }

    public LocalDateTime getEndScheduleDate() {
        return endScheduleDate;
    }

    public void setEndScheduleDate(LocalDateTime endScheduleDate) {
        this.endScheduleDate = endScheduleDate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public LocalDateTime getFinishScheduleDate() {
        return finishScheduleDate;
    }

    public void setFinishScheduleDate(LocalDateTime finishScheduleDate) {
        this.finishScheduleDate = finishScheduleDate;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public LocalDateTime getStartTreatmentSchedule() {
        return startTreatmentSchedule;
    }

    public void setStartTreatmentSchedule(LocalDateTime startTreatmentSchedule) {
        this.startTreatmentSchedule = startTreatmentSchedule;
    }

    public LocalDateTime getEndTreatmentSchedule() {
        return endTreatmentSchedule;
    }

    public void setEndTreatmentSchedule(LocalDateTime endTreatmentSchedule) {
        this.endTreatmentSchedule = endTreatmentSchedule;
    }

    public LocalDateTime getCandidateFirstMonth() {
        return candidateFirstMonth;
    }

    public void setCandidateFirstMonth(LocalDateTime candidateFirstMonth) {
        this.candidateFirstMonth = candidateFirstMonth;
    }

    public LocalDateTime getCandidateSecondMonth() {
        return candidateSecondMonth;
    }

    public void setCandidateSecondMonth(LocalDateTime candidateSecondMonth) {
        this.candidateSecondMonth = candidateSecondMonth;
    }

    public LocalDateTime getCandidateThirdMonth() {
        return candidateThirdMonth;
    }

    public void setCandidateThirdMonth(LocalDateTime candidateThirdMonth) {
        this.candidateThirdMonth = candidateThirdMonth;
    }
}

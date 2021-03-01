package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SalonSearchResponseDTO {
    private Long salonId;
    private String areaName;
    private String salonName;
    private String address;
    private String regularHoliday;
    private Short parkingLot;
    private String nearestStation;
    private String commitmentCondition;
    private List<String> commitmentConditions;
    private String salonBusinessHours;
    private LocalTime startHour;
    private LocalTime endHour;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String slug;
    private String commitmentConditionName;
    private List<String> listCommitmentConditionName;
    public SalonSearchResponseDTO() {
    }

    public SalonSearchResponseDTO(Long salonId, String areaName, String salonName, String address, String regularHoliday, Short parkingLot, String nearestStation, String commitmentCondition, List<String> commitmentConditions, String salonBusinessHours, LocalTime startHour, LocalTime endHour, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String slug, String commitmentConditionName, List<String> listCommitmentConditionName) {
        this.salonId = salonId;
        this.areaName = areaName;
        this.salonName = salonName;
        this.address = address;
        this.regularHoliday = regularHoliday;
        this.parkingLot = parkingLot;
        this.nearestStation = nearestStation;
        this.commitmentCondition = commitmentCondition;
        this.commitmentConditions = commitmentConditions;
        this.salonBusinessHours = salonBusinessHours;
        this.startHour = startHour;
        this.endHour = endHour;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.slug = slug;
        this.commitmentConditionName = commitmentConditionName;
        this.listCommitmentConditionName = listCommitmentConditionName;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegularHoliday() {
        return regularHoliday;
    }

    public void setRegularHoliday(String regularHoliday) {
        this.regularHoliday = regularHoliday;
    }

    public Short getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(Short parkingLot) {
        this.parkingLot = parkingLot;
    }

    public String getNearestStation() {
        return nearestStation;
    }

    public void setNearestStation(String nearestStation) {
        this.nearestStation = nearestStation;
    }

    public String getCommitmentCondition() {
        return commitmentCondition;
    }

    public void setCommitmentCondition(String commitmentCondition) {
        this.commitmentCondition = commitmentCondition;
    }

    public List<String> getCommitmentConditions() {
        return commitmentConditions;
    }

    public void setCommitmentConditions(List<String> commitmentConditions) {
        this.commitmentConditions = commitmentConditions;
    }

    public String getSalonBusinessHours() {
        return salonBusinessHours;
    }

    public void setSalonBusinessHours(String salonBusinessHours) {
        this.salonBusinessHours = salonBusinessHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCommitmentConditionName() {
        return commitmentConditionName;
    }

    public void setCommitmentConditionName(String commitmentConditionName) {
        this.commitmentConditionName = commitmentConditionName;
    }

    public List<String> getListCommitmentConditionName() {
        return listCommitmentConditionName;
    }

    public void setListCommitmentConditionName(List<String> listCommitmentConditionName) {
        this.listCommitmentConditionName = listCommitmentConditionName;
    }
}

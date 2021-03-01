package vn.viettel.core.dto.salon;

import vn.viettel.core.dto.booking.BookingDateDetailResponseDTO;

import java.util.List;

public class HairdresserWorkingRoutineResponseDTO {
    private Long salonId;

    private Long hairdresserId;

    private String hairdresserName;

    private Long workingStatus;

    private String workingStatusName;

    private Integer totalMilestoneBooked;

    private List<HairdresserDateRoutineDTO> salonDays;

    private List<HairdresserDateRoutineDTO> specificWorkingDays;

    private List<HairdresserDateRoutineDTO> absentDays;

    private List<BookingDateDetailResponseDTO> workingHourTable;

    public HairdresserWorkingRoutineResponseDTO() {
    }

    public HairdresserWorkingRoutineResponseDTO(Long salonId, Long hairdresserId, String hairdresserName, Long workingStatus, String workingStatusName, Integer totalMilestoneBooked, List<HairdresserDateRoutineDTO> salonDays, List<HairdresserDateRoutineDTO> specificWorkingDays, List<HairdresserDateRoutineDTO> absentDays, List<BookingDateDetailResponseDTO> workingHourTable) {
        this.salonId = salonId;
        this.hairdresserId = hairdresserId;
        this.hairdresserName = hairdresserName;
        this.workingStatus = workingStatus;
        this.workingStatusName = workingStatusName;
        this.totalMilestoneBooked = totalMilestoneBooked;
        this.salonDays = salonDays;
        this.specificWorkingDays = specificWorkingDays;
        this.absentDays = absentDays;
        this.workingHourTable = workingHourTable;
    }

    public List<BookingDateDetailResponseDTO> getWorkingHourTable() {
        return workingHourTable;
    }

    public void setWorkingHourTable(List<BookingDateDetailResponseDTO> workingHourTable) {
        this.workingHourTable = workingHourTable;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getHairdresserId() {
        return hairdresserId;
    }

    public void setHairdresserId(Long hairdresserId) {
        this.hairdresserId = hairdresserId;
    }

    public String getHairdresserName() {
        return hairdresserName;
    }

    public void setHairdresserName(String hairdresserName) {
        this.hairdresserName = hairdresserName;
    }

    public Long getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(Long workingStatus) {
        this.workingStatus = workingStatus;
    }

    public String getWorkingStatusName() {
        return workingStatusName;
    }

    public void setWorkingStatusName(String workingStatusName) {
        this.workingStatusName = workingStatusName;
    }

    public List<HairdresserDateRoutineDTO> getSalonDays() {
        return salonDays;
    }

    public void setSalonDays(List<HairdresserDateRoutineDTO> salonDays) {
        this.salonDays = salonDays;
    }

    public List<HairdresserDateRoutineDTO> getSpecificWorkingDays() {
        return specificWorkingDays;
    }

    public void setSpecificWorkingDays(List<HairdresserDateRoutineDTO> specificWorkingDays) {
        this.specificWorkingDays = specificWorkingDays;
    }

    public List<HairdresserDateRoutineDTO> getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(List<HairdresserDateRoutineDTO> absentDays) {
        this.absentDays = absentDays;
    }

    public Integer getTotalMilestoneBooked() {
        return totalMilestoneBooked;
    }

    public void setTotalMilestoneBooked(Integer totalMilestoneBooked) {
        this.totalMilestoneBooked = totalMilestoneBooked;
    }
}

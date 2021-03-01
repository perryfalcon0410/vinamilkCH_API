package vn.viettel.core.dto.booking;

import java.time.LocalDate;
import java.util.List;

public class BookingCalendarResponseDTO {
    private List<BookingDateDetailResponseDTO> dateList;

    private List<LocalDate> offDate;

    public BookingCalendarResponseDTO() {
    }

    public BookingCalendarResponseDTO(List<BookingDateDetailResponseDTO> dateList, List<LocalDate> offDate) {
        this.dateList = dateList;
        this.offDate = offDate;
    }

    public List<BookingDateDetailResponseDTO> getDateList() {
        return dateList;
    }

    public void setDateList(List<BookingDateDetailResponseDTO> dateList) {
        this.dateList = dateList;
    }

    public List<LocalDate> getOffDate() {
        return offDate;
    }

    public void setOffDate(List<LocalDate> offDate) {
        this.offDate = offDate;
    }
}

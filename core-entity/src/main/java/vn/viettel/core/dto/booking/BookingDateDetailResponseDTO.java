package vn.viettel.core.dto.booking;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDateDetailResponseDTO {
    private long id;

    private LocalDate currentDate;

    private String stringTime;

    private LocalTime time;

    private Boolean isBooked;

    private Boolean isWorked;

    public BookingDateDetailResponseDTO() {
    }

    public BookingDateDetailResponseDTO(long id, LocalDate currentDate, String stringTime, LocalTime time, Boolean isBooked, Boolean isWorked) {
        this.id = id;
        this.currentDate = currentDate;
        this.stringTime = stringTime;
        this.time = time;
        this.isBooked = isBooked;
        this.isWorked = isWorked;
    }

    public BookingDateDetailResponseDTO(long id, LocalDate currentDate, String stringTime, LocalTime time, Boolean isBooked) {
        this.id = id;
        this.currentDate = currentDate;
        this.stringTime = stringTime;
        this.time = time;
        this.isBooked = isBooked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStringTime() {
        return stringTime;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }

    public Boolean getBooked() {
        return isBooked;
    }

    public void setBooked(Boolean booked) {
        isBooked = booked;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Boolean getWorked() {
        return isWorked;
    }

    public void setWorked(Boolean worked) {
        isWorked = worked;
    }
}

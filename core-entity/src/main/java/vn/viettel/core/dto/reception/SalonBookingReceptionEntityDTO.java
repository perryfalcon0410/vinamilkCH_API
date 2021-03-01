package vn.viettel.core.dto.reception;

import vn.viettel.core.db.entity.Booking;
import vn.viettel.core.db.entity.Company2;
import vn.viettel.core.db.entity.Reception;
import vn.viettel.core.db.entity.Salon;

public class SalonBookingReceptionEntityDTO {
    private Salon salon;

    private Booking booking;

    private Reception reception;

    private Company2 company;

    public SalonBookingReceptionEntityDTO() {
    }

    public SalonBookingReceptionEntityDTO(Salon salon, Booking booking, Reception reception, Company2 company) {
        this.salon = salon;
        this.booking = booking;
        this.reception = reception;
        this.company = company;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Reception getReception() {
        return reception;
    }

    public void setReception(Reception reception) {
        this.reception = reception;
    }

    public Company2 getCompany() {
        return company;
    }

    public void setCompany(Company2 company) {
        this.company = company;
    }
}

package vn.viettel.core.dto.reception;

import vn.viettel.core.db.entity.Booking;
import vn.viettel.core.db.entity.Company;
import vn.viettel.core.db.entity.Reception;
import vn.viettel.core.db.entity.Salon;

public class SalonBookingReceptionEntityDTO {
    private Salon salon;

    private Booking booking;

    private Reception reception;

    private Company company;

    public SalonBookingReceptionEntityDTO() {
    }

    public SalonBookingReceptionEntityDTO(Salon salon, Booking booking, Reception reception, Company company) {
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}

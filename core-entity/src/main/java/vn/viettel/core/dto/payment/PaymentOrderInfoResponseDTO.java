package vn.viettel.core.dto.payment;

import vn.viettel.core.db.entity.OrderProductDetail;
import vn.viettel.core.db.entity.ReceptionCoupon;
import vn.viettel.core.db.entity.ReceptionDetail;
import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;
import vn.viettel.core.dto.salon.SalonConfirmationStyleDetailDTO;

import java.util.List;

public class PaymentOrderInfoResponseDTO {
    private Long bookingId;
    private List<ReceptionDetail> menus;
    private List<ReceptionCoupon> coupons;
    private SalonConfirmationHairdresserDetailDTO hairdresser;
    private List<SalonConfirmationStyleDetailDTO> hairStyles;
    private List<SalonConfirmationStyleDetailDTO> hairDesigns;
    private List<OrderProductDetail> productSales;
    private Double totalWithoutTax = 0.0;
    private Double tax = 0.0;
    private Double total = 0.0;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public List<ReceptionDetail> getMenus() {
        return menus;
    }

    public void setMenus(List<ReceptionDetail> menus) {
        this.menus = menus;
    }

    public List<ReceptionCoupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<ReceptionCoupon> coupons) {
        this.coupons = coupons;
    }

    public SalonConfirmationHairdresserDetailDTO getHairdresser() {
        return hairdresser;
    }

    public void setHairdresser(SalonConfirmationHairdresserDetailDTO hairdresser) {
        this.hairdresser = hairdresser;
    }

    public List<SalonConfirmationStyleDetailDTO> getHairStyles() {
        return hairStyles;
    }

    public void setHairStyles(List<SalonConfirmationStyleDetailDTO> hairStyles) {
        this.hairStyles = hairStyles;
    }

    public List<SalonConfirmationStyleDetailDTO> getHairDesigns() {
        return hairDesigns;
    }

    public void setHairDesigns(List<SalonConfirmationStyleDetailDTO> hairDesigns) {
        this.hairDesigns = hairDesigns;
    }

    public List<OrderProductDetail> getProductSales() {
        return productSales;
    }

    public void setProductSales(List<OrderProductDetail> productSales) {
        this.productSales = productSales;
    }

    public Double getTotalWithoutTax() {
        return totalWithoutTax;
    }

    public void setTotalWithoutTax(Double totalWithoutTax) {
        this.totalWithoutTax = totalWithoutTax;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}

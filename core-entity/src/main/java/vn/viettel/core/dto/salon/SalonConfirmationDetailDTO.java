package vn.viettel.core.dto.salon;

import vn.viettel.core.dto.payment.DirectOrderDetailDTO;
import vn.viettel.core.dto.payment.PaymentProductDetailDTO;
import vn.viettel.core.dto.waiting.ReceptionSubManagementResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class SalonConfirmationDetailDTO {
    private Long receptionId;

    private String receiptId;

    private Long bookingId;

    private String bookingCode;

    private LocalDateTime startScheduleDate;

    private LocalDateTime endScheduleDate;

    private LocalDateTime startTreatmentSchedule;

    private LocalDateTime endTreatmentSchedule;

    private LocalDateTime finishScheduleDate;

    private Long status;

    private Long companyId;

    private String companyName;

    private Long salonId;

    private String salonSlug;

    private String salonName;

    private String salonTel;

    private String salonZipcode;

    private String salonAddress;

    private Long memberId;

    private String memberQrCode;

    private SalonConfirmationMemberDetailDTO memberDetail;

    List<Long> salonMenuIds;

    List<SalonConfirmationMenuDetailDTO> salonMenus;

    List<Long> salonMainMenuIds;

    List<SalonConfirmationMenuDetailDTO> salonMainMenus;

    List<Long> salonRecommendMenuIds;

    List<SalonConfirmationMenuDetailDTO> salonRecommendMenus;

    private Double orderMenuTotal;

    Long couponId;

    SalonConfirmationCouponDetailDTO couponDetail;

    String couponIds;

    List<SalonConfirmationCouponDetailDTO> couponDetails;

    private Double totalCouponSubtract;

    private Long salonHairdresserId;

    private SalonConfirmationHairdresserDetailDTO salonHairdresserDetail;

    private Long salonStyleId;

    private SalonConfirmationStyleDetailDTO salonStyle;

    private Long salonDesignStyleId;

    private SalonConfirmationStyleDetailDTO salonDesignStyle;

    private Long salonStyleGroupId;

    private Double totalWithoutTax;

    private List<SalonConfirmationBookingHairPhotoDetailDTO> photoUrls;

    private Double tax;

    private Double total;

    private boolean notBookingYet;

    private String statusName;

    private Long bookingReferrer;

    private String bookingReferrerName;

    private List<ReceptionSubManagementResponseDTO> salonSubHairdresserDetails;

    private String memo;

    private Boolean saveAll;

    private List<PaymentProductDetailDTO> products;

    private Double totalProductCost;

    private List<DirectOrderDetailDTO> directOrders;

    private Double totalDirectOrderCost;

    private Double pointGaining;

    private Double pointUsing;

    private Double pointAddingManual;

    private Double prepayCost;

    private Double changeCost;

    private Double aftermathCost;

    private Double aftermathTax;

    private Double aftermathTotal;

    public SalonConfirmationDetailDTO() {
    }

    public SalonConfirmationDetailDTO(Long receptionId, String receiptId, Long bookingId, String bookingCode, LocalDateTime startScheduleDate, LocalDateTime endScheduleDate, LocalDateTime startTreatmentSchedule, LocalDateTime endTreatmentSchedule, LocalDateTime finishScheduleDate, Long status, Long companyId, String companyName, Long salonId, String salonSlug, String salonName, String salonTel, String salonZipcode, String salonAddress, Long memberId, String memberQrCode, SalonConfirmationMemberDetailDTO memberDetail, List<Long> salonMenuIds, List<SalonConfirmationMenuDetailDTO> salonMenus, List<Long> salonMainMenuIds, List<SalonConfirmationMenuDetailDTO> salonMainMenus, List<Long> salonRecommendMenuIds, List<SalonConfirmationMenuDetailDTO> salonRecommendMenus, Double orderMenuTotal, Long couponId, SalonConfirmationCouponDetailDTO couponDetail, String couponIds, List<SalonConfirmationCouponDetailDTO> couponDetails, Double totalCouponSubtract, Long salonHairdresserId, SalonConfirmationHairdresserDetailDTO salonHairdresserDetail, Long salonStyleId, SalonConfirmationStyleDetailDTO salonStyle, Long salonDesignStyleId, SalonConfirmationStyleDetailDTO salonDesignStyle, Long salonStyleGroupId, Double totalWithoutTax, List<SalonConfirmationBookingHairPhotoDetailDTO> photoUrls, Double tax, Double total, boolean notBookingYet, String statusName, Long bookingReferrer, String bookingReferrerName, List<ReceptionSubManagementResponseDTO> salonSubHairdresserDetails, String memo, Boolean saveAll, List<PaymentProductDetailDTO> products, Double totalProductCost, List<DirectOrderDetailDTO> directOrders, Double totalDirectOrderCost, Double pointGaining, Double pointUsing, Double pointAddingManual, Double prepayCost, Double changeCost, Double aftermathCost, Double aftermathTax, Double aftermathTotal) {
        this.receptionId = receptionId;
        this.receiptId = receiptId;
        this.bookingId = bookingId;
        this.bookingCode = bookingCode;
        this.startScheduleDate = startScheduleDate;
        this.endScheduleDate = endScheduleDate;
        this.startTreatmentSchedule = startTreatmentSchedule;
        this.endTreatmentSchedule = endTreatmentSchedule;
        this.finishScheduleDate = finishScheduleDate;
        this.status = status;
        this.companyId = companyId;
        this.companyName = companyName;
        this.salonId = salonId;
        this.salonSlug = salonSlug;
        this.salonName = salonName;
        this.salonTel = salonTel;
        this.salonZipcode = salonZipcode;
        this.salonAddress = salonAddress;
        this.memberId = memberId;
        this.memberQrCode = memberQrCode;
        this.memberDetail = memberDetail;
        this.salonMenuIds = salonMenuIds;
        this.salonMenus = salonMenus;
        this.salonMainMenuIds = salonMainMenuIds;
        this.salonMainMenus = salonMainMenus;
        this.salonRecommendMenuIds = salonRecommendMenuIds;
        this.salonRecommendMenus = salonRecommendMenus;
        this.orderMenuTotal = orderMenuTotal;
        this.couponId = couponId;
        this.couponDetail = couponDetail;
        this.couponIds = couponIds;
        this.couponDetails = couponDetails;
        this.totalCouponSubtract = totalCouponSubtract;
        this.salonHairdresserId = salonHairdresserId;
        this.salonHairdresserDetail = salonHairdresserDetail;
        this.salonStyleId = salonStyleId;
        this.salonStyle = salonStyle;
        this.salonDesignStyleId = salonDesignStyleId;
        this.salonDesignStyle = salonDesignStyle;
        this.salonStyleGroupId = salonStyleGroupId;
        this.totalWithoutTax = totalWithoutTax;
        this.photoUrls = photoUrls;
        this.tax = tax;
        this.total = total;
        this.notBookingYet = notBookingYet;
        this.statusName = statusName;
        this.bookingReferrer = bookingReferrer;
        this.bookingReferrerName = bookingReferrerName;
        this.salonSubHairdresserDetails = salonSubHairdresserDetails;
        this.memo = memo;
        this.saveAll = saveAll;
        this.products = products;
        this.totalProductCost = totalProductCost;
        this.directOrders = directOrders;
        this.totalDirectOrderCost = totalDirectOrderCost;
        this.pointGaining = pointGaining;
        this.pointUsing = pointUsing;
        this.pointAddingManual = pointAddingManual;
        this.prepayCost = prepayCost;
        this.changeCost = changeCost;
        this.aftermathCost = aftermathCost;
        this.aftermathTax = aftermathTax;
        this.aftermathTotal = aftermathTotal;
    }

    public String getSalonTel() {
        return salonTel;
    }

    public void setSalonTel(String salonTel) {
        this.salonTel = salonTel;
    }

    public String getSalonZipcode() {
        return salonZipcode;
    }

    public void setSalonZipcode(String salonZipcode) {
        this.salonZipcode = salonZipcode;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public void setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getAftermathTax() {
        return aftermathTax;
    }

    public void setAftermathTax(Double aftermathTax) {
        this.aftermathTax = aftermathTax;
    }

    public Double getAftermathTotal() {
        return aftermathTotal;
    }

    public void setAftermathTotal(Double aftermathTotal) {
        this.aftermathTotal = aftermathTotal;
    }

    public Double getAftermathCost() {
        return aftermathCost;
    }

    public void setAftermathCost(Double aftermathCost) {
        this.aftermathCost = aftermathCost;
    }

    public Double getPointAddingManual() {
        return pointAddingManual;
    }

    public void setPointAddingManual(Double pointAddingManual) {
        this.pointAddingManual = pointAddingManual;
    }

    public Double getPointGaining() {
        return pointGaining;
    }

    public void setPointGaining(Double pointGaining) {
        this.pointGaining = pointGaining;
    }

    public Double getPointUsing() {
        return pointUsing;
    }

    public void setPointUsing(Double pointUsing) {
        this.pointUsing = pointUsing;
    }

    public Double getPrepayCost() {
        return prepayCost;
    }

    public void setPrepayCost(Double prepayCost) {
        this.prepayCost = prepayCost;
    }

    public Double getTotalProductCost() {
        return totalProductCost;
    }

    public void setTotalProductCost(Double totalProductCost) {
        this.totalProductCost = totalProductCost;
    }

    public Double getTotalCouponSubtract() {
        return totalCouponSubtract;
    }

    public void setTotalCouponSubtract(Double totalCouponSubtract) {
        this.totalCouponSubtract = totalCouponSubtract;
    }

    public Double getOrderMenuTotal() {
        return orderMenuTotal;
    }

    public void setOrderMenuTotal(Double orderMenuTotal) {
        this.orderMenuTotal = orderMenuTotal;
    }

    public List<PaymentProductDetailDTO> getProducts() {
        return products;
    }

    public void setProducts(List<PaymentProductDetailDTO> products) {
        this.products = products;
    }

    public Boolean getSaveAll() {
        return saveAll;
    }

    public void setSaveAll(Boolean saveAll) {
        this.saveAll = saveAll;
    }

    public List<ReceptionSubManagementResponseDTO> getSalonSubHairdresserDetails() {
        return salonSubHairdresserDetails;
    }

    public void setSalonSubHairdresserDetails(List<ReceptionSubManagementResponseDTO> salonSubHairdresserDetails) {
        this.salonSubHairdresserDetails = salonSubHairdresserDetails;
    }

    public Long getSalonStyleGroupId() {
        return salonStyleGroupId;
    }

    public void setSalonStyleGroupId(Long salonStyleGroupId) {
        this.salonStyleGroupId = salonStyleGroupId;
    }

    public Long getSalonDesignStyleId() {
        return salonDesignStyleId;
    }

    public void setSalonDesignStyleId(Long salonDesignStyleId) {
        this.salonDesignStyleId = salonDesignStyleId;
    }

    public SalonConfirmationStyleDetailDTO getSalonDesignStyle() {
        return salonDesignStyle;
    }

    public void setSalonDesignStyle(SalonConfirmationStyleDetailDTO salonDesignStyle) {
        this.salonDesignStyle = salonDesignStyle;
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public SalonConfirmationMemberDetailDTO getMemberDetail() {
        return memberDetail;
    }

    public void setMemberDetail(SalonConfirmationMemberDetailDTO memberDetail) {
        this.memberDetail = memberDetail;
    }

    public List<Long> getSalonMenuIds() {
        return salonMenuIds;
    }

    public void setSalonMenuIds(List<Long> salonMenuIds) {
        this.salonMenuIds = salonMenuIds;
    }

    public List<SalonConfirmationMenuDetailDTO> getSalonMenus() {
        return salonMenus;
    }

    public void setSalonMenus(List<SalonConfirmationMenuDetailDTO> salonMenus) {
        this.salonMenus = salonMenus;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public SalonConfirmationCouponDetailDTO getCouponDetail() {
        return couponDetail;
    }

    public void setCouponDetail(SalonConfirmationCouponDetailDTO couponDetail) {
        this.couponDetail = couponDetail;
    }

    public Long getSalonHairdresserId() {
        return salonHairdresserId;
    }

    public void setSalonHairdresserId(Long salonHairdresserId) {
        this.salonHairdresserId = salonHairdresserId;
    }

    public SalonConfirmationHairdresserDetailDTO getSalonHairdresserDetail() {
        return salonHairdresserDetail;
    }

    public void setSalonHairdresserDetail(SalonConfirmationHairdresserDetailDTO salonHairdresserDetail) {
        this.salonHairdresserDetail = salonHairdresserDetail;
    }

    public Long getSalonStyleId() {
        return salonStyleId;
    }

    public void setSalonStyleId(Long salonStyleId) {
        this.salonStyleId = salonStyleId;
    }

    public SalonConfirmationStyleDetailDTO getSalonStyle() {
        return salonStyle;
    }

    public void setSalonStyle(SalonConfirmationStyleDetailDTO salonStyle) {
        this.salonStyle = salonStyle;
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

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public String getMemberQrCode() {
        return memberQrCode;
    }

    public void setMemberQrCode(String memberQrCode) {
        this.memberQrCode = memberQrCode;
    }

    public String getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(String couponIds) {
        this.couponIds = couponIds;
    }

    public List<SalonConfirmationCouponDetailDTO> getCouponDetails() {
        return couponDetails;
    }

    public void setCouponDetails(List<SalonConfirmationCouponDetailDTO> couponDetails) {
        this.couponDetails = couponDetails;
    }

    public List<SalonConfirmationBookingHairPhotoDetailDTO> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<SalonConfirmationBookingHairPhotoDetailDTO> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public boolean isNotBookingYet() {
        return notBookingYet;
    }

    public void setNotBookingYet(boolean notBookingYet) {
        this.notBookingYet = notBookingYet;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getBookingReferrer() {
        return bookingReferrer;
    }

    public void setBookingReferrer(Long bookingReferrer) {
        this.bookingReferrer = bookingReferrer;
    }

    public String getBookingReferrerName() {
        return bookingReferrerName;
    }

    public void setBookingReferrerName(String bookingReferrerName) {
        this.bookingReferrerName = bookingReferrerName;
    }

    public List<Long> getSalonMainMenuIds() {
        return salonMainMenuIds;
    }

    public void setSalonMainMenuIds(List<Long> salonMainMenuIds) {
        this.salonMainMenuIds = salonMainMenuIds;
    }

    public List<SalonConfirmationMenuDetailDTO> getSalonMainMenus() {
        return salonMainMenus;
    }

    public void setSalonMainMenus(List<SalonConfirmationMenuDetailDTO> salonMainMenus) {
        this.salonMainMenus = salonMainMenus;
    }

    public List<Long> getSalonRecommendMenuIds() {
        return salonRecommendMenuIds;
    }

    public void setSalonRecommendMenuIds(List<Long> salonRecommendMenuIds) {
        this.salonRecommendMenuIds = salonRecommendMenuIds;
    }

    public List<SalonConfirmationMenuDetailDTO> getSalonRecommendMenus() {
        return salonRecommendMenus;
    }

    public void setSalonRecommendMenus(List<SalonConfirmationMenuDetailDTO> salonRecommendMenus) {
        this.salonRecommendMenus = salonRecommendMenus;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<DirectOrderDetailDTO> getDirectOrders() {
        return directOrders;
    }

    public void setDirectOrders(List<DirectOrderDetailDTO> directOrders) {
        this.directOrders = directOrders;
    }

    public Double getTotalDirectOrderCost() {
        return totalDirectOrderCost;
    }

    public void setTotalDirectOrderCost(Double totalDirectOrderCost) {
        this.totalDirectOrderCost = totalDirectOrderCost;
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

    public LocalDateTime getFinishScheduleDate() {
        return finishScheduleDate;
    }

    public void setFinishScheduleDate(LocalDateTime finishScheduleDate) {
        this.finishScheduleDate = finishScheduleDate;
    }

    public Double getChangeCost() {
        return changeCost;
    }

    public void setChangeCost(Double changeCost) {
        this.changeCost = changeCost;
    }
}

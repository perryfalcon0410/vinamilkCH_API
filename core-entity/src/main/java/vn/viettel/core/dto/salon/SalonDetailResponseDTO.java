package vn.viettel.core.dto.salon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SalonDetailResponseDTO {
    private Long salonId;
    private String name;
    private String slug;
    private String tel;
    private String address;
    private List<SalonBusinessHourResponseDTO> salonBusinessHour;
    private String regularHoliday;
    private Short parkingLot;
    private String paymentMethod;
    private String nearestStation;
    private String website;
    private BigDecimal latitude;
    private BigDecimal longtitude;
    private String photoUrl;
    private List<String> photoUrls;
    private String commitmentCondition;
    private List<String> commitmentConditions;
    private String facebook;
    private String twitter;
    private String instagram;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public SalonDetailResponseDTO() {
    }

    public SalonDetailResponseDTO(Long salonId, String name, String slug, String tel, String address, List<SalonBusinessHourResponseDTO> salonBusinessHour, String regularHoliday, Short parkingLot, String paymentMethod, String nearestStation, String website, BigDecimal latitude, BigDecimal longtitude, String photoUrl, List<String> photoUrls, String commitmentCondition, List<String> commitmentConditions, String facebook, String twitter, String instagram, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.salonId = salonId;
        this.name = name;
        this.slug = slug;
        this.tel = tel;
        this.address = address;
        this.salonBusinessHour = salonBusinessHour;
        this.regularHoliday = regularHoliday;
        this.parkingLot = parkingLot;
        this.paymentMethod = paymentMethod;
        this.nearestStation = nearestStation;
        this.website = website;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.photoUrl = photoUrl;
        this.photoUrls = photoUrls;
        this.commitmentCondition = commitmentCondition;
        this.commitmentConditions = commitmentConditions;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<SalonBusinessHourResponseDTO> getSalonBusinessHour() {
        return salonBusinessHour;
    }

    public void setSalonBusinessHour(List<SalonBusinessHourResponseDTO> salonBusinessHour) {
        this.salonBusinessHour = salonBusinessHour;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNearestStation() {
        return nearestStation;
    }

    public void setNearestStation(String nearestStation) {
        this.nearestStation = nearestStation;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(BigDecimal longtitude) {
        this.longtitude = longtitude;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
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

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}

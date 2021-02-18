package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "salons")
@AttributeOverride(name = "id", column = @Column(name = "salon_id"))
public class Salon extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "salon_type")
    private Long salonType;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "tel")
    private String tel;

    @Column(name = "address")
    private String address;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "country")
    private String country;

    @Column(name = "regular_holiday")
    private String regularHoliday;

    @Column(name = "parking_lot")
    private Short parkingLot;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "nearest_station")
    private String nearestStation;

    @Column(name = "website")
    private String website;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longtitude")
    private BigDecimal longtitude;

    @Column(name = "other_remarks")
    private String otherRemarks;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "salon_category_id")
    private Long salonCategoryId;

    @Column(name = "salon_ip")
    private String salonIP;

    @Column(name = "email")
    private String email;

    @Column(name = "square_access_token")
    private String squareAccessToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getSalonType() {
        return salonType;
    }

    public void setSalonType(Long salonType) {
        this.salonType = salonType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {

        this.status = status;
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public String getOtherRemarks() {
        return otherRemarks;
    }

    public void setOtherRemarks(String otherRemarks) {
        this.otherRemarks = otherRemarks;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public void setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
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

    public Long getSalonCategoryId() {
        return salonCategoryId;
    }

    public void setSalonCategoryId(Long salonCategoryId) {
        this.salonCategoryId = salonCategoryId;
    }

    public String getSalonIP() {
        return salonIP;
    }

    public void setSalonIP(String salonIP) {
        this.salonIP = salonIP;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSquareAccessToken() {
        return squareAccessToken;
    }

    public void setSquareAccessToken(String squareAccessToken) {
        this.squareAccessToken = squareAccessToken;
    }
}

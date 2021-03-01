package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "basic_shops")
public class BasicShop extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "name_kana")
    private String nameKana;

    @Column(name = "shop_type_id")
    private Long shopTypeId;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "address")
    private String address;

    @Column(name = "tel", length = 16)
    private String tel;

    @Column(name = "fax", length = 16)
    private String fax;

    @Column(name = "has_parking_lot")
    private boolean hasParkingLot;

    @Column(name = "email")
    private String email;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "picture1_url")
    private String picture1Url;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "business_day")
    private String businessDay;

    @Column(name = "nearest_station")
    private String nearestStation;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "line_url")
    private String lineUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana) {
        this.nameKana = nameKana;
    }

    public Long getShopTypeId() {
        return shopTypeId;
    }

    public void setShopTypeId(Long shopTypeId) {
        this.shopTypeId = shopTypeId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public boolean isHasParkingLot() {
        return hasParkingLot;
    }

    public void setHasParkingLot(boolean hasParkingLot) {
        this.hasParkingLot = hasParkingLot;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getPicture1Url() {
        return picture1Url;
    }

    public void setPicture1Url(String picture1Url) {
        this.picture1Url = picture1Url;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getBusinessDay() {
        return businessDay;
    }

    public void setBusinessDay(String businessDay) {
        this.businessDay = businessDay;
    }

    public String getNearestStation() {
        return nearestStation;
    }

    public void setNearestStation(String nearestStation) {
        this.nearestStation = nearestStation;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getLineUrl() {
        return lineUrl;
    }

    public void setLineUrl(String lineUrl) {
        this.lineUrl = lineUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }
}

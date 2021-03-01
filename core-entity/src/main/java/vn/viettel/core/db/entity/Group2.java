package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "\"groups\"")
public class Group2 extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_code", length = 12)
    private String groupCode;

    @Column(name = "name")
    private String name;

    @Column(name = "name_kana")
    private String nameKana;

    @Column(name = "group_type_id")
    private Long groupTypeId;

    @Column(name = "officer_name")
    private String officerName;

    @Column(name = "open_status")
    private boolean openStatus;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "tel", length = 16)
    private String tel;

    @Column(name = "fax", length = 16)
    private String fax;

    @Column(name = "has_parking_lot")
    private boolean hasParkingLot;

    @Column(name = "business_day")
    private String businessDay;

    @Column(name = "nearest_station")
    private String nearestStation;

    @Column(name = "email")
    private String email;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "picture1_url")
    private String picture1Url;

    @Column(name = "picture2_url")
    private String picture2Url;

    @Column(name = "picture3_url")
    private String picture3Url;

    @Column(name = "picture4_url")
    private String picture4Url;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "line_url")
    private String lineUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "is_publish")
    private boolean publish;

    @Column(name = "payjp_customer_id", length = 64)
    private String payjpCustomerId;

    @Column(name = "security_policy")
    private String securityPolicy;

    @Column(name = "distributor_id")
    private Long distributorId;

    @Column(name = "allow_distributor_login")
    private Boolean allowDistributorLogin;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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

    public Long getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(Long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public boolean isOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(boolean openStatus) {
        this.openStatus = openStatus;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPicture2Url() {
        return picture2Url;
    }

    public void setPicture2Url(String picture2Url) {
        this.picture2Url = picture2Url;
    }

    public String getPicture3Url() {
        return picture3Url;
    }

    public void setPicture3Url(String picture3Url) {
        this.picture3Url = picture3Url;
    }

    public String getPicture4Url() {
        return picture4Url;
    }

    public void setPicture4Url(String picture4Url) {
        this.picture4Url = picture4Url;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public void setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
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

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public String getPayjpCustomerId() {
        return payjpCustomerId;
    }

    public void setPayjpCustomerId(String payjpCustomerId) {
        this.payjpCustomerId = payjpCustomerId;
    }

    public String getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(String securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    public Long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Long distributorId) {
        this.distributorId = distributorId;
    }

    public Boolean getAllowDistributorLogin() {
        return allowDistributorLogin;
    }

    public void setAllowDistributorLogin(Boolean allowDistributorLogin) {
        this.allowDistributorLogin = allowDistributorLogin;
    }
}

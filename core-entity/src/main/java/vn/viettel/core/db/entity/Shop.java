package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "shops")
public class Shop extends BaseEntity {

    @Column(name = "shop_code", length = 12, nullable = true)
    private String shopCode;

    @Column(name = "name", length = 64, nullable = true)
    private String name;

    @Column(name = "name_kana", length = 64, nullable = true)
    private String nameKana;

    @Column(name = "shop_type_id", nullable = true)
    private Long shopTypeId;

    @Column(name = "owner_name", length = 32, nullable = true)
    private String ownerName;

    @Column(name = "address", length = 128, nullable = true)
    private String address;

    @Column(name = "tel", length = 128, nullable = true)
    private String tel;

    @Column(name = "fax", length = 128, nullable = true)
    private String fax;

    @Column(name = "parking", nullable = true)
    private Integer parking;

    @Column(name = "email", length = 64, nullable = true)
    private String email;

    @Column(name = "favicon_url", nullable = true)
    private String faviconUrl;

    @Column(name = "logo_url", nullable = true)
    private String logoUrl;

    @Column(name = "website_url", length = 128, nullable = true)
    private String websiteUrl;

    @Column(name = "picture1_url", nullable = true)
    private String picture1Url;

    @Column(name = "picture2_url", nullable = true)
    private String picture2Url;

    @Column(name = "picture3_url", nullable = true)
    private String picture3Url;

    @Column(name = "picture4_url", nullable = true)
    private String picture4Url;

    @Column(name = "latitude", precision = 8, scale = 6, nullable = true)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6, nullable = true)
    private BigDecimal longitude;

    @Column(name = "status", nullable = false)
    private Integer status = 0;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "postal_code", length = 10, nullable = true)
    private String postalCode;

    @Column(name = "business_day", nullable = true)
    private String businessDay;

    @Column(name = "nearest_station", length = 128, nullable = true)
    private String nearestStation;

    @Column(name = "available_card", length = 10, nullable = true)
    private String availableCard;

    @Column(name = "payjp_customer_id", length = 64, nullable = true)
    private String payjpCustomerId;

    @Column(name = "menu_text_1", length = 64, nullable = true)
    private String menuText1;

    @Column(name = "menu_text_2", length = 64, nullable = true)
    private String menuText2;

    @Column(name = "menu_text_3", length = 64, nullable = true)
    private String menuText3;

    @Column(name = "menu_text_4", length = 64, nullable = true)
    private String menuText4;

    @Column(name = "menu_picture1_url", nullable = true)
    private String menuPicture1Url;

    @Column(name = "menu_picture2_url", nullable = true)
    private String menuPicture2Url;

    @Column(name = "menu_picture3_url", nullable = true)
    private String menuPicture3Url;

    @Column(name = "menu_picture4_url", nullable = true)
    private String menuPicture4Url;

    @Column(name = "facebook_url", nullable = true)
    private String facebookUrl;

    @Column(name = "line_url", nullable = true)
    private String lineUrl;

    @Column(name = "twitter_url", nullable = true)
    private String twitterUrl;

    @Column(name = "is_publish")
    private boolean publish;

    @Column(name = "security_policy")
    private String securityPolicy;

    @Column(name = "distributor_id")
    private Long distributorId;

    @Column(name = "allow_distributor_login")
    private Boolean allowDistributorLogin;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "shop")
    @JsonBackReference
    private List<ShopMenuSetting> menuSettings;

    public Shop() {
        super();
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
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

    public Integer getParking() {
        return parking;
    }

    public void setParking(Integer parking) {
        this.parking = parking;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getAvailableCard() {
        return availableCard;
    }

    public void setAvailableCard(String availableCard) {
        this.availableCard = availableCard;
    }

    public String getPayjpCustomerId() {
        return payjpCustomerId;
    }

    public void setPayjpCustomerId(String payjpCustomerId) {
        this.payjpCustomerId = payjpCustomerId;
    }

    public String getMenuText1() {
        return menuText1;
    }

    public void setMenuText1(String menuText1) {
        this.menuText1 = menuText1;
    }

    public String getMenuText2() {
        return menuText2;
    }

    public void setMenuText2(String menuText2) {
        this.menuText2 = menuText2;
    }

    public String getMenuText3() {
        return menuText3;
    }

    public void setMenuText3(String menuText3) {
        this.menuText3 = menuText3;
    }

    public String getMenuText4() {
        return menuText4;
    }

    public void setMenuText4(String menuText4) {
        this.menuText4 = menuText4;
    }

    public String getMenuPicture1Url() {
        return menuPicture1Url;
    }

    public void setMenuPicture1Url(String menuPicture1Url) {
        this.menuPicture1Url = menuPicture1Url;
    }

    public String getMenuPicture2Url() {
        return menuPicture2Url;
    }

    public void setMenuPicture2Url(String menuPicture2Url) {
        this.menuPicture2Url = menuPicture2Url;
    }

    public String getMenuPicture3Url() {
        return menuPicture3Url;
    }

    public void setMenuPicture3Url(String menuPicture3Url) {
        this.menuPicture3Url = menuPicture3Url;
    }

    public String getMenuPicture4Url() {
        return menuPicture4Url;
    }

    public void setMenuPicture4Url(String menuPicture4Url) {
        this.menuPicture4Url = menuPicture4Url;
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

    public String getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(String securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    public List<ShopMenuSetting> getMenuSettings() {
        return menuSettings;
    }

    public void setMenuSettings(List<ShopMenuSetting> menuSettings) {
        this.menuSettings = menuSettings;
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

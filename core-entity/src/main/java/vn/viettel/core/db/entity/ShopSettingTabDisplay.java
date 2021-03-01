package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.CompanyProductLayoutType;
import vn.viettel.core.db.entity.status.converter.CompanyProductLayoutTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_setting_tab_displays")
public class ShopSettingTabDisplay extends BaseEntity {

    @Column(name = "shop_id", nullable = false)
    private long shopId;

    @Column(name = "is_show_shop_information")
    private boolean showShopInformation;

    @Column(name = "is_show_shop_access")
    private boolean showShopAccess;

    @Column(name = "is_show_shop_reservation")
    private boolean showShopReservation;

    @Column(name = "is_show_shop_notice")
    private boolean showShopNotice;

    @Column(name = "is_show_shop_inquiry")
    private boolean showShopInquiry;

    @Column(name = "is_show_shop_event_reservation")
    private boolean showShopEventReservation;

    @Column(name = "is_show_shop_point_coupon")
    private boolean showShopPointCoupon;

    @Column(name = "google_analytics_id")
    private String googleAnalyticsId;

    @Column(name = "pc_picture_url")
    private String pcPictureUrl;

    @Column(name = "mobile_picture_url")
    private String mobilePictureUrl;

    @Column(name = "font_japanese")
    private String fontJapanese;

    @Column(name = "font_english")
    private String fontEnglish;

    @Column(name = "introduction_title")
    private String introductionTitle;

    @Column(name = "introduction_layout")
    @Convert(converter = CompanyProductLayoutTypeConverter.class)
    private CompanyProductLayoutType introductionLayout;

    @Column(name = "introduction_picture_url")
    private String introductionPictureUrl;

    @Column(name = "introduction_text")
    private String introductionText;

    @Column(name = "description")
    private String description;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public boolean isShowShopInformation() {
        return showShopInformation;
    }

    public void setShowShopInformation(boolean showShopInformation) {
        this.showShopInformation = showShopInformation;
    }

    public boolean isShowShopAccess() {
        return showShopAccess;
    }

    public void setShowShopAccess(boolean showShopAccess) {
        this.showShopAccess = showShopAccess;
    }

    public boolean isShowShopReservation() {
        return showShopReservation;
    }

    public void setShowShopReservation(boolean showShopReservation) {
        this.showShopReservation = showShopReservation;
    }

    public boolean isShowShopNotice() {
        return showShopNotice;
    }

    public void setShowShopNotice(boolean showShopNotice) {
        this.showShopNotice = showShopNotice;
    }

    public boolean isShowShopInquiry() {
        return showShopInquiry;
    }

    public void setShowShopInquiry(boolean showShopInquiry) {
        this.showShopInquiry = showShopInquiry;
    }

    public boolean isShowShopEventReservation() {
        return showShopEventReservation;
    }

    public void setShowShopEventReservation(boolean showShopEventReservation) {
        this.showShopEventReservation = showShopEventReservation;
    }

    public boolean isShowShopPointCoupon() {
        return showShopPointCoupon;
    }

    public void setShowShopPointCoupon(boolean showShopPointCoupon) {
        this.showShopPointCoupon = showShopPointCoupon;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    public String getPcPictureUrl() {
        return pcPictureUrl;
    }

    public void setPcPictureUrl(String pcPictureUrl) {
        this.pcPictureUrl = pcPictureUrl;
    }

    public String getMobilePictureUrl() {
        return mobilePictureUrl;
    }

    public void setMobilePictureUrl(String mobilePictureUrl) {
        this.mobilePictureUrl = mobilePictureUrl;
    }

    public String getFontJapanese() {
        return fontJapanese;
    }

    public void setFontJapanese(String fontJapanese) {
        this.fontJapanese = fontJapanese;
    }

    public String getFontEnglish() {
        return fontEnglish;
    }

    public void setFontEnglish(String fontEnglish) {
        this.fontEnglish = fontEnglish;
    }

    public String getIntroductionTitle() {
        return introductionTitle;
    }

    public void setIntroductionTitle(String introductionTitle) {
        this.introductionTitle = introductionTitle;
    }

    public CompanyProductLayoutType getIntroductionLayout() {
        return introductionLayout;
    }

    public void setIntroductionLayout(CompanyProductLayoutType introductionLayout) {
        this.introductionLayout = introductionLayout;
    }

    public String getIntroductionPictureUrl() {
        return introductionPictureUrl;
    }

    public void setIntroductionPictureUrl(String introductionPictureUrl) {
        this.introductionPictureUrl = introductionPictureUrl;
    }

    public String getIntroductionText() {
        return introductionText;
    }

    public void setIntroductionText(String introductionText) {
        this.introductionText = introductionText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.CompanyProductLayoutType;
import vn.viettel.core.db.entity.status.converter.CompanyProductLayoutTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_setting_tab_displays")
public class GroupSettingTabDisplay extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "is_show_group_information", nullable = false)
    private boolean showGroupInformation;

    @Column(name = "is_show_affiliated_shop", nullable = false)
    private boolean showAffiliatedShop;

    @Column(name = "is_show_group_access", nullable = false)
    private boolean shopGroupAcccess;

    @Column(name = "is_show_group_reservation", nullable = false)
    private boolean showGroupReservation;

    @Column(name = "is_show_group_notice")
    private boolean showGroupNotice;

    @Column(name = "is_show_group_inquiry")
    private boolean showGroupInquiry;

    @Column(name = "is_show_group_event_reservation")
    private boolean showGroupEventReservation;

    @Column(name = "is_show_group_point_coupon")
    private boolean showGroupPointCoupon;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public boolean isShowGroupInformation() {
        return showGroupInformation;
    }

    public void setShowGroupInformation(boolean showGroupInformation) {
        this.showGroupInformation = showGroupInformation;
    }

    public boolean isShowAffiliatedShop() {
        return showAffiliatedShop;
    }

    public void setShowAffiliatedShop(boolean showAffiliatedShop) {
        this.showAffiliatedShop = showAffiliatedShop;
    }

    public boolean isShopGroupAcccess() {
        return shopGroupAcccess;
    }

    public void setShopGroupAcccess(boolean shopGroupAcccess) {
        this.shopGroupAcccess = shopGroupAcccess;
    }

    public boolean isShowGroupReservation() {
        return showGroupReservation;
    }

    public void setShowGroupReservation(boolean showGroupReservation) {
        this.showGroupReservation = showGroupReservation;
    }

    public boolean isShowGroupNotice() {
        return showGroupNotice;
    }

    public void setShowGroupNotice(boolean showGroupNotice) {
        this.showGroupNotice = showGroupNotice;
    }

    public boolean isShowGroupInquiry() {
        return showGroupInquiry;
    }

    public void setShowGroupInquiry(boolean showGroupInquiry) {
        this.showGroupInquiry = showGroupInquiry;
    }

    public boolean isShowGroupEventReservation() {
        return showGroupEventReservation;
    }

    public void setShowGroupEventReservation(boolean showGroupEventReservation) {
        this.showGroupEventReservation = showGroupEventReservation;
    }

    public boolean isShowGroupPointCoupon() {
        return showGroupPointCoupon;
    }

    public void setShowGroupPointCoupon(boolean showGroupPointCoupon) {
        this.showGroupPointCoupon = showGroupPointCoupon;
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

package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.CompanyProductLayoutType;
import vn.viettel.core.db.entity.status.converter.CompanyProductLayoutTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "company_tab_setting_displays")
public class CompanySettingTabDisplay extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

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

    @Column(name = "show_information", nullable = false)
    private boolean showInformation;

    @Column(name = "show_notice", nullable = false)
    private boolean showNotice;

    @Column(name = "show_product", nullable = false)
    private boolean showProduct;

    @Column(name = "show_reservation", nullable = false)
    private boolean showReservation;

    @Column(name = "show_recruitment", nullable = false)
    private boolean showRecruitment;

    @Column(name = "show_inquiry", nullable = false)
    private boolean showInquiry;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public boolean isShowInformation() {
        return showInformation;
    }

    public void setShowInformation(boolean showInformation) {
        this.showInformation = showInformation;
    }

    public boolean isShowNotice() {
        return showNotice;
    }

    public void setShowNotice(boolean showNotice) {
        this.showNotice = showNotice;
    }

    public boolean isShowProduct() {
        return showProduct;
    }

    public void setShowProduct(boolean showProduct) {
        this.showProduct = showProduct;
    }

    public boolean isShowReservation() {
        return showReservation;
    }

    public void setShowReservation(boolean showReservation) {
        this.showReservation = showReservation;
    }

    public boolean isShowRecruitment() {
        return showRecruitment;
    }

    public void setShowRecruitment(boolean showRecruitment) {
        this.showRecruitment = showRecruitment;
    }

    public boolean isShowInquiry() {
        return showInquiry;
    }

    public void setShowInquiry(boolean showInquiry) {
        this.showInquiry = showInquiry;
    }
}

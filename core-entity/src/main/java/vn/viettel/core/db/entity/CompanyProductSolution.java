package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.CompanyProductLayoutType;
import vn.viettel.core.db.entity.status.converter.CompanyProductLayoutTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_products")
public class CompanyProductSolution extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "name")
    private String name;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;

    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;

    @Column(name = "thumbnail_picture_url")
    private String thumbnailPictureUrl;

    @Column(name = "overview")
    private String overview;

    @Column(name = "section1_title")
    private String section1Title;

    @Column(name = "section1_layout")
    @Convert(converter = CompanyProductLayoutTypeConverter.class)
    private CompanyProductLayoutType section1Layout;

    @Column(name = "section1_picture_url")
    private String section1PictureUrl;

    @Column(name = "section1_text")
    private String section1Text;

    @Column(name = "section2_title")
    private String section2Title;

    @Column(name = "section2_layout")
    @Convert(converter = CompanyProductLayoutTypeConverter.class)
    private CompanyProductLayoutType section2Layout;

    @Column(name = "section2_picture_url")
    private String section2PictureUrl;

    @Column(name = "section2_text")
    private String section2Text;

    @Column(name = "section3_title")
    private String section3Title;

    @Column(name = "section3_layout")
    @Convert(converter = CompanyProductLayoutTypeConverter.class)
    private CompanyProductLayoutType section3Layout;

    @Column(name = "section3_picture_url")
    private String section3PictureUrl;

    @Column(name = "section3_text")
    private String section3Text;

    @Column(name = "section4_title")
    private String section4Title;

    @Column(name = "section4_layout")
    @Convert(converter = CompanyProductLayoutTypeConverter.class)
    private CompanyProductLayoutType section4Layout;

    @Column(name = "section4_picture_url")
    private String section4PictureUrl;

    @Column(name = "section4_text")
    private String section4Text;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(LocalDateTime saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public LocalDateTime getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(LocalDateTime saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public String getThumbnailPictureUrl() {
        return thumbnailPictureUrl;
    }

    public void setThumbnailPictureUrl(String thumbnailPictureUrl) {
        this.thumbnailPictureUrl = thumbnailPictureUrl;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getSection1Title() {
        return section1Title;
    }

    public void setSection1Title(String section1Title) {
        this.section1Title = section1Title;
    }

    public CompanyProductLayoutType getSection1Layout() {
        return section1Layout;
    }

    public void setSection1Layout(CompanyProductLayoutType section1Layout) {
        this.section1Layout = section1Layout;
    }

    public String getSection1PictureUrl() {
        return section1PictureUrl;
    }

    public void setSection1PictureUrl(String section1PictureUrl) {
        this.section1PictureUrl = section1PictureUrl;
    }

    public String getSection1Text() {
        return section1Text;
    }

    public void setSection1Text(String section1Text) {
        this.section1Text = section1Text;
    }

    public String getSection2Title() {
        return section2Title;
    }

    public void setSection2Title(String section2Title) {
        this.section2Title = section2Title;
    }

    public CompanyProductLayoutType getSection2Layout() {
        return section2Layout;
    }

    public void setSection2Layout(CompanyProductLayoutType section2Layout) {
        this.section2Layout = section2Layout;
    }

    public String getSection2PictureUrl() {
        return section2PictureUrl;
    }

    public void setSection2PictureUrl(String section2PictureUrl) {
        this.section2PictureUrl = section2PictureUrl;
    }

    public String getSection2Text() {
        return section2Text;
    }

    public void setSection2Text(String section2Text) {
        this.section2Text = section2Text;
    }

    public String getSection3Title() {
        return section3Title;
    }

    public void setSection3Title(String section3Title) {
        this.section3Title = section3Title;
    }

    public CompanyProductLayoutType getSection3Layout() {
        return section3Layout;
    }

    public void setSection3Layout(CompanyProductLayoutType section3Layout) {
        this.section3Layout = section3Layout;
    }

    public String getSection3PictureUrl() {
        return section3PictureUrl;
    }

    public void setSection3PictureUrl(String section3PictureUrl) {
        this.section3PictureUrl = section3PictureUrl;
    }

    public String getSection3Text() {
        return section3Text;
    }

    public void setSection3Text(String section3Text) {
        this.section3Text = section3Text;
    }

    public String getSection4Title() {
        return section4Title;
    }

    public void setSection4Title(String section4Title) {
        this.section4Title = section4Title;
    }

    public CompanyProductLayoutType getSection4Layout() {
        return section4Layout;
    }

    public void setSection4Layout(CompanyProductLayoutType section4Layout) {
        this.section4Layout = section4Layout;
    }

    public String getSection4PictureUrl() {
        return section4PictureUrl;
    }

    public void setSection4PictureUrl(String section4PictureUrl) {
        this.section4PictureUrl = section4PictureUrl;
    }

    public String getSection4Text() {
        return section4Text;
    }

    public void setSection4Text(String section4Text) {
        this.section4Text = section4Text;
    }

}

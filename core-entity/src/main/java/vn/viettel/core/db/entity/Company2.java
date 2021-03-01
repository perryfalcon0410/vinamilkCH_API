package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "\"companies\"")
@AttributeOverride(name = "id", column = @Column(name = "company_id"))
public class Company2 extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private boolean status;

    @Column(name = "tel")
    private String tel;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "created_by")
    private Long createdBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}

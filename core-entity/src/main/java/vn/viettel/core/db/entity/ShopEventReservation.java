package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_event_reservation")
public class ShopEventReservation extends BaseEntity {

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enable", nullable = false)
    private boolean enable;

    @Column(name = "event_start_date", nullable = false)
    private LocalDateTime eventStartDate;

    @Column(name = "event_end_date", nullable = false)
    private LocalDateTime eventEndDate;

    @Column(name = "application_start_date")
    private LocalDateTime applicationStartDate;

    @Column(name = "application_end_date")
    private LocalDateTime applicationEndDate;

    @Column(name = "place")
    private String place;

    @Column(name = "address_of_place")
    private String addressOfPlace;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "unit_price")
    private Long unitPrice;

    @Column(name = "price")
    private Long price;

    @Column(name = "recruitment_number", nullable = false)
    private Long recruitmentNumber;

    @Column(name = "thumbnail_picture_url")
    private String thumbnailPictureUrl;

    @Column(name = "map_picture_url")
    private String mapPictureUrl;

    @Column(name = "application_button")
    private String applicationButton;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public LocalDateTime getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public LocalDateTime getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDateTime applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDateTime getApplicationEndDate() {
        return applicationEndDate;
    }

    public void setApplicationEndDate(LocalDateTime applicationEndDate) {
        this.applicationEndDate = applicationEndDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddressOfPlace() {
        return addressOfPlace;
    }

    public void setAddressOfPlace(String addressOfPlace) {
        this.addressOfPlace = addressOfPlace;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getRecruitmentNumber() {
        return recruitmentNumber;
    }

    public void setRecruitmentNumber(Long recruitmentNumber) {
        this.recruitmentNumber = recruitmentNumber;
    }

    public String getThumbnailPictureUrl() {
        return thumbnailPictureUrl;
    }

    public void setThumbnailPictureUrl(String thumbnailPictureUrl) {
        this.thumbnailPictureUrl = thumbnailPictureUrl;
    }

    public String getMapPictureUrl() {
        return mapPictureUrl;
    }

    public void setMapPictureUrl(String mapPictureUrl) {
        this.mapPictureUrl = mapPictureUrl;
    }

    public String getApplicationButton() {
        return applicationButton;
    }

    public void setApplicationButton(String applicationButton) {
        this.applicationButton = applicationButton;
    }
}

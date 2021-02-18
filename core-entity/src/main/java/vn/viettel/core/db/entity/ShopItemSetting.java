package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_item_settings")
public class ShopItemSetting extends BaseEntity {

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "name")
    private String name;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Column(name = "reserve_in_advance_days")
    private int reserveInAdvanceDays;

    @Column(name = "cleaning_time")
    private String cleaningTime;

    @Column(name = "description")
    private String description;

    @Column(name = "picture1_url")
    private String picture1Url;

    @Column(name = "picture2_url")
    private String picture2Url;

    @Column(name = "picture3_url")
    private String picture3Url;

    @Column(name = "picture4_url")
    private String picture4Url;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "hide_in_customer", nullable = false)
    private Boolean hideInCustomer;

    public ShopItemSetting() {
    }

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

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public int getReserveInAdvanceDays() {
        return reserveInAdvanceDays;
    }

    public void setReserveInAdvanceDays(int reserveInAdvanceDays) {
        this.reserveInAdvanceDays = reserveInAdvanceDays;
    }

    public String getCleaningTime() {
        return cleaningTime;
    }

    public void setCleaningTime(String cleaningTime) {
        this.cleaningTime = cleaningTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getHideInCustomer() {
        return hideInCustomer;
    }

    public void setHideInCustomer(Boolean hideInCustomer) {
        this.hideInCustomer = hideInCustomer;
    }
}
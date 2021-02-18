package vn.viettel.authorization.service.dto.shop;

import vn.viettel.core.db.entity.status.MenuPriceCalculatedMethod;
import vn.viettel.core.service.dto.BaseDTO;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class MenuSettingDTO extends BaseDTO {

    private ShopDTO shop;
    private String name;
    private Long itemId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime from;
    private LocalDateTime to;
    private boolean hasEndDate;
    private String weekdays;
    private Long unitPrice;
    private Long valuePrice;
    private Long unitTime;
    private Long valueTime;
    private boolean isAdvancePayment;
    private Long cancelBy7Days;
    private Long cancelBy6To4Days;
    private Long cancelBy3To1Days;
    private Long cancelBy0Days;
    private String description;
    private String picture1Url;
    private String picture2Url;
    private String picture3Url;
    private String picture4Url;
    private Boolean hideInCustomer;
    private MenuPriceCalculatedMethod priceCalculatedMethod;

    public ShopDTO getShop() {
        return shop;
    }

    public void setShop(ShopDTO shop) {
        this.shop = shop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public boolean isHasEndDate() {
        return hasEndDate;
    }

    public void setHasEndDate(boolean hasEndDate) {
        this.hasEndDate = hasEndDate;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getValuePrice() {
        return valuePrice;
    }

    public void setValuePrice(Long valuePrice) {
        this.valuePrice = valuePrice;
    }

    public Long getUnitTime() {
        return unitTime;
    }

    public void setUnitTime(Long unitTime) {
        this.unitTime = unitTime;
    }

    public Long getValueTime() {
        return valueTime;
    }

    public void setValueTime(Long valueTime) {
        this.valueTime = valueTime;
    }

    public boolean isAdvancePayment() {
        return isAdvancePayment;
    }

    public void setAdvancePayment(boolean advancePayment) {
        isAdvancePayment = advancePayment;
    }

    public Long getCancelBy7Days() {
        return cancelBy7Days;
    }

    public void setCancelBy7Days(Long cancelBy7Days) {
        this.cancelBy7Days = cancelBy7Days;
    }

    public Long getCancelBy6To4Days() {
        return cancelBy6To4Days;
    }

    public void setCancelBy6To4Days(Long cancelBy6To4Days) {
        this.cancelBy6To4Days = cancelBy6To4Days;
    }

    public Long getCancelBy3To1Days() {
        return cancelBy3To1Days;
    }

    public void setCancelBy3To1Days(Long cancelBy3To1Days) {
        this.cancelBy3To1Days = cancelBy3To1Days;
    }

    public Long getCancelBy0Days() {
        return cancelBy0Days;
    }

    public void setCancelBy0Days(Long cancelBy0Days) {
        this.cancelBy0Days = cancelBy0Days;
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

    public Boolean getHideInCustomer() {
        return hideInCustomer;
    }

    public void setHideInCustomer(Boolean hideInCustomer) {
        this.hideInCustomer = hideInCustomer;
    }

    public MenuPriceCalculatedMethod getPriceCalculatedMethod() {
        return priceCalculatedMethod;
    }

    public void setPriceCalculatedMethod(MenuPriceCalculatedMethod priceCalculatedMethod) {
        this.priceCalculatedMethod = priceCalculatedMethod;
    }
}

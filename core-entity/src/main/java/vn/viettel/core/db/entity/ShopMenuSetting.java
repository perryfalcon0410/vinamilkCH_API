package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import vn.viettel.core.db.entity.status.MenuPriceCalculatedMethod;
import vn.viettel.core.db.entity.status.MenuType;
import vn.viettel.core.db.entity.status.PercentageTax;
import vn.viettel.core.db.entity.status.TaxType;
import vn.viettel.core.db.entity.status.converter.MenuPriceCalculatedMethodConverter;
import vn.viettel.core.db.entity.status.converter.MenuTypeConverter;
import vn.viettel.core.db.entity.status.converter.PercentageTaxConverter;
import vn.viettel.core.db.entity.status.converter.TaxTypeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shop_menu_settings")
public class ShopMenuSetting extends BaseEntity {

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "type")
    @Convert(converter = MenuTypeConverter.class)
    private MenuType type;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "from_date")
    private LocalDateTime from;

    @Column(name = "to_date")
    private LocalDateTime to;

    @Column(name = "has_end_date")
    private Boolean hasEndDate;

    @Column(name = "weekdays", length = 7)
    private String weekdays;

    @Column(name = "is_tax_included")
    @Convert(converter = TaxTypeConverter.class)
    private TaxType isTaxIncluded;

    @Column(name = "increased_tax")
    @Convert(converter = PercentageTaxConverter.class)
    private PercentageTax increasedTax;

    @Column(name = "unit_price")
    private Long unitPrice;

    @Column(name = "value_price")
    private Long valuePrice;

    @Column(name = "unit_time")
    private Long unitTime;

    @Column(name = "value_time")
    private Long valueTime;

    @Column(name = "need_approval")
    private boolean needApproval;

    @Column(name = "number_per_day")
    private Long numberPerDay;

    @Column(name = "is_advance_payment", nullable = false)
    private boolean isAdvancePayment;

    @Column(name = "cancel_by_7_days")
    private Long cancelBy7Days;

    @Column(name = "cancel_by_6_to_4_days")
    private Long cancelBy6To4Days;

    @Column(name = "cancel_by_3_to_1_days")
    private Long cancelBy3To1Days;

    @Column(name = "cancel_by_0_days")
    private Long cancelBy0Days;

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

    @Column(name = "hide_in_customer", nullable = false)
    private Boolean hideInCustomer;

    @Column(name = "price_calculated_method", nullable = false)
    @Convert(converter = MenuPriceCalculatedMethodConverter.class)
    private MenuPriceCalculatedMethod priceCalculatedMethod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    @JsonManagedReference
    private Shop shop;

    public ShopMenuSetting() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public void setType(MenuType type) {
        this.type = type;
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

    public Boolean getHasEndDate() {
        return hasEndDate;
    }

    public void setHasEndDate(Boolean hasEndDate) {
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

    public TaxType getIsTaxIncluded() {
        return isTaxIncluded;
    }

    public void setIsTaxIncluded(TaxType isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public PercentageTax getIncreasedTax() {
        return increasedTax;
    }

    public void setIncreasedTax(PercentageTax increasedTax) {
        this.increasedTax = increasedTax;
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

    public Shop getShop() {
        return shop;
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

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public boolean isNeedApproval() {
        return needApproval;
    }

    public void setNeedApproval(boolean needApproval) {
        this.needApproval = needApproval;
    }

    public Long getNumberPerDay() {
        return numberPerDay;
    }

    public void setNumberPerDay(Long numberPerDay) {
        this.numberPerDay = numberPerDay;
    }
}
package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.SettingReservationMethod;
import vn.viettel.core.db.entity.status.converter.SettingReservationMethodConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_setting_reservation_methods")
public class ShopSettingReservationMethod extends BaseEntity {

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_sunday", nullable = false)
    private SettingReservationMethod methodSunday;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_monday", nullable = false)
    private SettingReservationMethod methodMonday;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_tuesday", nullable = false)
    private SettingReservationMethod methodTuesday;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_wednesday", nullable = false)
    private SettingReservationMethod methodWednesday;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_thursday", nullable = false)
    private SettingReservationMethod methodThursday;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_friday", nullable = false)
    private SettingReservationMethod methodFriday;

    @Convert(converter = SettingReservationMethodConverter.class)
    @Column(name = "method_saturday", nullable = false)
    private SettingReservationMethod methodSaturday;

    @Column(name = "cancel_by_14_days", nullable = false)
    private Integer cancelBy14Days;

    @Column(name = "cancel_by_13_days", nullable = false)
    private Integer cancelBy13Days;

    @Column(name = "cancel_by_12_days", nullable = false)
    private Integer cancelBy12Days;

    @Column(name = "cancel_by_11_days", nullable = false)
    private Integer cancelBy11Days;

    @Column(name = "cancel_by_10_days", nullable = false)
    private Integer cancelBy10Days;

    @Column(name = "cancel_by_9_days", nullable = false)
    private Integer cancelBy9Days;

    @Column(name = "cancel_by_8_days", nullable = false)
    private Integer cancelBy8Days;

    @Column(name = "cancel_by_7_days", nullable = false)
    private Integer cancelBy7Days;

    @Column(name = "cancel_by_6_days", nullable = false)
    private Integer cancelBy6Days;

    @Column(name = "cancel_by_5_days", nullable = false)
    private Integer cancelBy5Days;

    @Column(name = "cancel_by_4_days", nullable = false)
    private Integer cancelBy4Days;

    @Column(name = "cancel_by_3_days", nullable = false)
    private Integer cancelBy3Days;

    @Column(name = "cancel_by_2_days", nullable = false)
    private Integer cancelBy2Days;

    @Column(name = "cancel_by_1_days", nullable = false)
    private Integer cancelBy1Days;

    @Column(name = "cancel_by_0_days", nullable = false)
    private Integer cancelBy0Days;

    @Column(name = "description", nullable = false, length = 4096)
    private String description;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public SettingReservationMethod getMethodSunday() {
        return methodSunday;
    }

    public void setMethodSunday(SettingReservationMethod methodSunday) {
        this.methodSunday = methodSunday;
    }

    public SettingReservationMethod getMethodMonday() {
        return methodMonday;
    }

    public void setMethodMonday(SettingReservationMethod methodMonday) {
        this.methodMonday = methodMonday;
    }

    public SettingReservationMethod getMethodTuesday() {
        return methodTuesday;
    }

    public void setMethodTuesday(SettingReservationMethod methodTuesday) {
        this.methodTuesday = methodTuesday;
    }

    public SettingReservationMethod getMethodWednesday() {
        return methodWednesday;
    }

    public void setMethodWednesday(SettingReservationMethod methodWednesday) {
        this.methodWednesday = methodWednesday;
    }

    public SettingReservationMethod getMethodThursday() {
        return methodThursday;
    }

    public void setMethodThursday(SettingReservationMethod methodThursday) {
        this.methodThursday = methodThursday;
    }

    public SettingReservationMethod getMethodFriday() {
        return methodFriday;
    }

    public void setMethodFriday(SettingReservationMethod methodFriday) {
        this.methodFriday = methodFriday;
    }

    public SettingReservationMethod getMethodSaturday() {
        return methodSaturday;
    }

    public void setMethodSaturday(SettingReservationMethod methodSaturday) {
        this.methodSaturday = methodSaturday;
    }

    public Integer getCancelBy14Days() {
        return cancelBy14Days;
    }

    public void setCancelBy14Days(Integer cancelBy14Days) {
        this.cancelBy14Days = cancelBy14Days;
    }

    public Integer getCancelBy13Days() {
        return cancelBy13Days;
    }

    public void setCancelBy13Days(Integer cancelBy13Days) {
        this.cancelBy13Days = cancelBy13Days;
    }

    public Integer getCancelBy12Days() {
        return cancelBy12Days;
    }

    public void setCancelBy12Days(Integer cancelBy12Days) {
        this.cancelBy12Days = cancelBy12Days;
    }

    public Integer getCancelBy11Days() {
        return cancelBy11Days;
    }

    public void setCancelBy11Days(Integer cancelBy11Days) {
        this.cancelBy11Days = cancelBy11Days;
    }

    public Integer getCancelBy10Days() {
        return cancelBy10Days;
    }

    public void setCancelBy10Days(Integer cancelBy10Days) {
        this.cancelBy10Days = cancelBy10Days;
    }

    public Integer getCancelBy9Days() {
        return cancelBy9Days;
    }

    public void setCancelBy9Days(Integer cancelBy9Days) {
        this.cancelBy9Days = cancelBy9Days;
    }

    public Integer getCancelBy8Days() {
        return cancelBy8Days;
    }

    public void setCancelBy8Days(Integer cancelBy8Days) {
        this.cancelBy8Days = cancelBy8Days;
    }

    public Integer getCancelBy7Days() {
        return cancelBy7Days;
    }

    public void setCancelBy7Days(Integer cancelBy7Days) {
        this.cancelBy7Days = cancelBy7Days;
    }

    public Integer getCancelBy6Days() {
        return cancelBy6Days;
    }

    public void setCancelBy6Days(Integer cancelBy6Days) {
        this.cancelBy6Days = cancelBy6Days;
    }

    public Integer getCancelBy5Days() {
        return cancelBy5Days;
    }

    public void setCancelBy5Days(Integer cancelBy5Days) {
        this.cancelBy5Days = cancelBy5Days;
    }

    public Integer getCancelBy4Days() {
        return cancelBy4Days;
    }

    public void setCancelBy4Days(Integer cancelBy4Days) {
        this.cancelBy4Days = cancelBy4Days;
    }

    public Integer getCancelBy3Days() {
        return cancelBy3Days;
    }

    public void setCancelBy3Days(Integer cancelBy3Days) {
        this.cancelBy3Days = cancelBy3Days;
    }

    public Integer getCancelBy2Days() {
        return cancelBy2Days;
    }

    public void setCancelBy2Days(Integer cancelBy2Days) {
        this.cancelBy2Days = cancelBy2Days;
    }

    public Integer getCancelBy1Days() {
        return cancelBy1Days;
    }

    public void setCancelBy1Days(Integer cancelBy1Days) {
        this.cancelBy1Days = cancelBy1Days;
    }

    public Integer getCancelBy0Days() {
        return cancelBy0Days;
    }

    public void setCancelBy0Days(Integer cancelBy0Days) {
        this.cancelBy0Days = cancelBy0Days;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

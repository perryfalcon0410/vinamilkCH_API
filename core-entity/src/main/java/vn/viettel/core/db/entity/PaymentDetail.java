package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.db.entity.status.converter.ObjectConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_details")
public class PaymentDetail extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Convert(converter = ObjectConverter.class)
    @Column(name = "object", nullable = false)
    private Object object;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "charge_id", nullable = false)
    private String chargeId;

    @Column(name = "init_charge_id", nullable = false)
    private String initChargeId;

    @Column(name = "sub_id", nullable = false)
    private String subId;

    @Column(name = "price", precision = 9, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "apply_date", nullable = false)
    private LocalDateTime applyDate;

    @Column(name = "settle_date")
    private LocalDateTime settleDate;

    @Column(name = "create_by")
    private Long createBy;

    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;

    @Column(name = "cancel_by")
    private Long cancelBy;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "incentive_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal incentiveRate;

    public PaymentDetail() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getInitChargeId() {
        return initChargeId;
    }

    public void setInitChargeId(String initChargeId) {
        this.initChargeId = initChargeId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public LocalDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public LocalDateTime getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(LocalDateTime settleDate) {
        this.settleDate = settleDate;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(LocalDateTime cancelDate) {
        this.cancelDate = cancelDate;
    }

    public Long getCancelBy() {
        return cancelBy;
    }

    public void setCancelBy(Long cancelBy) {
        this.cancelBy = cancelBy;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getIncentiveRate() {
        return incentiveRate;
    }

    public void setIncentiveRate(BigDecimal incentiveRate) {
        this.incentiveRate = incentiveRate;
    }
}

package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.Object;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
public class Plan extends BaseEntity {

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "object")
    private Object object;

    @Column(name = "plan_id", length = 32, nullable = false)
    private String planId;

    @Column(name = "price", precision = 9, scale = 2)
    private BigDecimal price;

    @Column(name = "init_price", precision = 9, scale = 2)
    private BigDecimal initPrice;

    @Column(name = "init_price_promotion", precision = 9, scale = 2)
    private BigDecimal initPricePromotion;

    @Column(name = "description")
    private String description;

    @Column(name = "category", length = 32)
    private String category;

    @Column(name = "type", length = 32)
    private String type;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "period_by_month")
    private Long periodByMonth;

    @Column(name = "start_date_init_promotion")
    private LocalDateTime startDateInitPromotion;

    @Column(name = "end_date_init_promotion")
    private LocalDateTime endDateInitPromotion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getInitPrice() {
        return initPrice;
    }

    public void setInitPrice(BigDecimal initPrice) {
        this.initPrice = initPrice;
    }

    public BigDecimal getInitPricePromotion() {
        return initPricePromotion;
    }

    public void setInitPricePromotion(BigDecimal initPricePromotion) {
        this.initPricePromotion = initPricePromotion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getPeriodByMonth() {
        return periodByMonth;
    }

    public void setPeriodByMonth(Long periodByMonth) {
        this.periodByMonth = periodByMonth;
    }

    public LocalDateTime getStartDateInitPromotion() {
        return startDateInitPromotion;
    }

    public void setStartDateInitPromotion(LocalDateTime startDateInitPromotion) {
        this.startDateInitPromotion = startDateInitPromotion;
    }

    public LocalDateTime getEndDateInitPromotion() {
        return endDateInitPromotion;
    }

    public void setEndDateInitPromotion(LocalDateTime endDateInitPromotion) {
        this.endDateInitPromotion = endDateInitPromotion;
    }
}

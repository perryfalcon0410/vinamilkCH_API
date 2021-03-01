package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "distributors")
public class Distributor extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "distributor_number", nullable = false)
    private String distributorNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "current_plan_incentive_rate", nullable = false)
    private BigDecimal currentPlanIncentiveRate;

    @Column(name = "next_plan_incentive_rate", nullable = false)
    private BigDecimal nextPlanIncentiveRate;

    @Column(name = "current_platform_incentive_rate", nullable = false)
    private BigDecimal currentPlatformIncentiveRate;

    @Column(name = "next_platform_incentive_rate", nullable = false)
    private BigDecimal nextPlatformIncentiveRate;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDistributorNumber() {
        return distributorNumber;
    }

    public void setDistributorNumber(String distributorNumber) {
        this.distributorNumber = distributorNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getCurrentPlanIncentiveRate() {
        return currentPlanIncentiveRate;
    }

    public void setCurrentPlanIncentiveRate(BigDecimal currentPlanIncentiveRate) {
        this.currentPlanIncentiveRate = currentPlanIncentiveRate;
    }

    public BigDecimal getNextPlanIncentiveRate() {
        return nextPlanIncentiveRate;
    }

    public void setNextPlanIncentiveRate(BigDecimal nextPlanIncentiveRate) {
        this.nextPlanIncentiveRate = nextPlanIncentiveRate;
    }

    public BigDecimal getCurrentPlatformIncentiveRate() {
        return currentPlatformIncentiveRate;
    }

    public void setCurrentPlatformIncentiveRate(BigDecimal currentPlatformIncentiveRate) {
        this.currentPlatformIncentiveRate = currentPlatformIncentiveRate;
    }

    public BigDecimal getNextPlatformIncentiveRate() {
        return nextPlatformIncentiveRate;
    }

    public void setNextPlatformIncentiveRate(BigDecimal nextPlatformIncentiveRate) {
        this.nextPlatformIncentiveRate = nextPlatformIncentiveRate;
    }
}

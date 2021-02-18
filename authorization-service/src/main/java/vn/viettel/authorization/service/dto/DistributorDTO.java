package vn.viettel.authorization.service.dto;

import vn.viettel.core.service.dto.BaseDTO;

import java.math.BigDecimal;

public class DistributorDTO extends BaseDTO {

    private Long userId;

    private String distributorNumber;

    private String phoneNumber;

    private String address;

    private BigDecimal currentPlanIncentiveRate;

    private BigDecimal nextPlanIncentiveRate;

    private BigDecimal currentPlatformIncentiveRate;

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

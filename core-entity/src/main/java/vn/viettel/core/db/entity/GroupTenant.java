package vn.viettel.core.db.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "group_tenants")
public class GroupTenant extends BaseEntity {
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;
	
	@Column(name = "payjp_tenant_id", length = 225, nullable = false)
    private String payjpTenantId;

    @Column(name = "platform_fee_rate", precision = 10, scale = 2)
    private BigDecimal platformFeeRate;

    @Column(name = "minimum_transfer_amount", precision = 10, scale = 2)
    private BigDecimal minimumTransferAmount;

    @Column(name = "bank_account_number", length = 225, nullable = true)
    private String bankAccountNumber;

    @Column(name = "bank_branch_code", length = 225, nullable = true)
    private String bankBranchCode;

    @Column(name = "bank_code", length = 225, nullable = true)
    private String bankCode;

    @Column(name = "bank_account_holder_name", length = 225, nullable = true)
    private String bankAccountHolderName;

    @Column(name = "bank_account_type", length = 225, nullable = true)
    private String bankAccountType;

    @Column(name = "bank_account_status", nullable = false)
    private Integer bankAccountStatus = 0;

    @Column(name = "currencies_supported", length = 225, nullable = true)
    private String currenciesSupported;

    @Column(name = "default_currency", length = 225, nullable = true)
    private String defaultCurrency;

    @Column(name = "reviewed_brands", length = 225, nullable = true)
    private String reviewedBrands;

    @Column(name = "is_active")
    private boolean isActive;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public String getPayjpTenantId() {
		return payjpTenantId;
	}

	public void setPayjpTenantId(String payjpTenantId) {
		this.payjpTenantId = payjpTenantId;
	}

	public BigDecimal getPlatformFeeRate() {
		return platformFeeRate;
	}

	public void setPlatformFeeRate(BigDecimal flatformFeeRate) {
		this.platformFeeRate = flatformFeeRate;
	}

	public BigDecimal getMinimumTransferAmount() {
		return minimumTransferAmount;
	}

	public void setMinimumTransferAmount(BigDecimal minimumTransferAmount) {
		this.minimumTransferAmount = minimumTransferAmount;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankBranchCode() {
		return bankBranchCode;
	}

	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankAccountHolderName() {
		return bankAccountHolderName;
	}

	public void setBankAccountHolderName(String bankAccountHolderName) {
		this.bankAccountHolderName = bankAccountHolderName;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public Integer getBankAccountStatus() {
		return bankAccountStatus;
	}

	public void setBankAccountStatus(Integer bankAccountStatus) {
		this.bankAccountStatus = bankAccountStatus;
	}

	public String getCurrenciesSupported() {
		return currenciesSupported;
	}

	public void setCurrenciesSupported(String currenciesSupported) {
		this.currenciesSupported = currenciesSupported;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public String getReviewedBrands() {
		return reviewedBrands;
	}

	public void setReviewedBrands(String reviewedBrands) {
		this.reviewedBrands = reviewedBrands;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

}

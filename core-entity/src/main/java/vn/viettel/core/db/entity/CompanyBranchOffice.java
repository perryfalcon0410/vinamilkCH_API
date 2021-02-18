package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "company_offices")
public class CompanyBranchOffice extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "branch_office_name")
    private String branchOfficeName;

    @Column(name = "branch_office_zip")
    private String branchOfficeZip;

    @Column(name = "branch_office_address")
    private String branchOfficeAddress;

    @Column(name = "branch_office_phone")
    private String branchOfficePhone;

    @Column(name = "branch_office_fax")
    private String branchOfficeFax;

    @Column(name = "branch_office_picture_url")
    private String branchOfficePictureUrl;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getBranchOfficeName() {
        return branchOfficeName;
    }

    public void setBranchOfficeName(String branchOfficeName) {
        this.branchOfficeName = branchOfficeName;
    }

    public String getBranchOfficeZip() {
        return branchOfficeZip;
    }

    public void setBranchOfficeZip(String branchOfficeZip) {
        this.branchOfficeZip = branchOfficeZip;
    }

    public String getBranchOfficeAddress() {
        return branchOfficeAddress;
    }

    public void setBranchOfficeAddress(String branchOfficeAddress) {
        this.branchOfficeAddress = branchOfficeAddress;
    }

    public String getBranchOfficePhone() {
        return branchOfficePhone;
    }

    public void setBranchOfficePhone(String branchOfficePhone) {
        this.branchOfficePhone = branchOfficePhone;
    }

    public String getBranchOfficeFax() {
        return branchOfficeFax;
    }

    public void setBranchOfficeFax(String branchOfficeFax) {
        this.branchOfficeFax = branchOfficeFax;
    }

    public String getBranchOfficePictureUrl() {
        return branchOfficePictureUrl;
    }

    public void setBranchOfficePictureUrl(String branchOfficePictureUrl) {
        this.branchOfficePictureUrl = branchOfficePictureUrl;
    }
}

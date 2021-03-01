package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "customer_reservation")
public class CompanyCustomerReservation extends BaseEntity {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "event_required")
    private String eventRequired;

    @Column(name = "program_required")
    private String programRequired;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "position")
    private String position;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "address")
    private String address;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getEventRequired() {
        return eventRequired;
    }

    public void setEventRequired(String eventRequired) {
        this.eventRequired = eventRequired;
    }

    public String getProgramRequired() {
        return programRequired;
    }

    public void setProgramRequired(String programRequired) {
        this.programRequired = programRequired;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CUSTOMERS")
public class Customer extends BaseEntity {
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "BAR_CODE")
    private String barCode;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "BIRTHDAY")
    private Date birthday;
    @Column(name = "GENDER")
    private Long gender;
    @Column(name = "CUSTOMER_GROUP_ID")
    private Long customerGroupId;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "SPECIAL_CUSTOMER")
    private Long specialCustomer;
    @Column(name = "NOTED")
    private String noted;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "COUNTRY_ID")
    private Long countryId;
    @Column(name = "AREA_ID")
    private Long areaId;
    @Column(name = "PROVINCE_ID")
    private Long provinceId;
    @Column(name = "DISTRICT_ID")
    private Long districtId;
    @Column(name = "WARD_ID")
    private Long wardId;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "IDENTITY_CARD_ID")
    private Long identityCardId;
    @Column(name = "COMPANY_NAME")
    private String companyName;
    @Column(name = "COMPANY_ADDRESS")
    private String companyAddress;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "CARD_MEMBER_ID")
    private Long cardMemberId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;

}


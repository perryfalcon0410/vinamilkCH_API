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
    @Column(name = "CUS_CODE")
    private String cusCode;
    @Column(name = "BAR_CODE")
    private String barCode;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "DOB")
    private Date DOB;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "GENDER")
    private int gender;
    @Column(name = "STATUS")
    private int status;
    @Column(name = "CUS_TYPE")
    private int cusType;
    @Column(name = "EXCLUSIVE")
    private boolean exclusive = false;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ADDRESS_ID")
    private long addressId;
    @Column(name = "SHOP_ID")
    private long shopId;
    @Column(name = "GROUP_ID")
    private long groupId;
    @Column(name = "IDENTITY_CARD_ID")
    private long idCardId;
    @Column(name = "CARD_MEMBER_ID")
    private long cardMemberId;
    @Column(name = "COMPANY_ID")
    private long companyId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;

}


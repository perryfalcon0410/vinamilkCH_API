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
@Table(name = "customers")
public class Customer extends BaseEntity {
    @Column(name = "cus_code")
    private String cusCode;
    @Column(name = "bar_code")
    private String barCode;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "tax_code")
    private String taxCode;
    private Date DOB;
    private int gender;
    private int status;
    @Column(name = "cus_type")
    private int cusType;
    private boolean exclusive = false;
    private String description;
    @Column(name = "address_id")
    private long addressId;
    @Column(name = "shop_id")
    private long shopId;
    @Column(name = "group_id")
    private long groupId;
    @Column(name = "identity_card_id")
    private long idCardId;
    @Column(name = "card_member_id")
    private long cardMemberId;
    @Column(name = "company_id")
    private long companyId;

}


package vn.viettel.core.db.entity.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CUSTOMERS")
public class Customer extends BaseEntity {
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "FIRST_NAME_NOT_ACCENT")
    private String firstNameNotAccent;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "LAST_NAME_NOT_ACCENT")
    private String lastNameNotAccent;
    @Column(name = "DOB")
    private Date dob;
    @Column(name = "PLACE_OF_BIRTH")
    private String placeOfBirth;
    @Column(name = "OCCUPATION_ID")
    private Long occupationId;
    @Column(name = "OCCUPATION_DESCR")
    private String occupationDescription;
    @Column(name = "MARITAL_STATUS_ID")
    private Long martialStatusId;
    @Column(name = "GENDER_ID")
    private Long genderId;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "ID_NO")
    private String idNo;
    @Column(name = "ID_NO_ISSUED_DATE")
    private Date idNoIssuedDate;
    @Column(name = "ID_NO_ISSUED_PLACE")
    private String idNoIssuedPlace;
    @Column(name = "PASSPORT_NO")
    private String passportNo;
    @Column(name = "PASSPORT_NO_ISSUED_DATE")
    private Date passportNoIssuedDate;
    @Column(name = "PASSPORT_NO_ISSUED_PLACE")
    private String passportNoIssuedPlace;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "FAX")
    private String fax;
    @Column(name = "MOBIPHONE")
    private String mobiPhone;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "AREA_ID")
    private Long areaId;
    @Column(name = "STREET")
    private String street;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "WORKING_OFFICE")
    private String workingOffice;
    @Column(name = "OFFICE_ADDRESS")
    private String officeAddress;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "OLD_CODE")
    private String oldCode;
    @Column(name = "BARCODE")
    private String barCode;
    @Column(name = "NAME_TEXT")
    private String nameText;
    @Column(name = "IS_PRIVATE")
    private Boolean isPrivate;
    @Column(name = "TOTAL_BILL_AMOUNT")
    private Float totalBill;
    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;
    @Column(name = "CLOSELY_TYPE_ID")
    private Long closelyTypeId;
    @Column(name = "CARD_TYPE_ID")
    private Long cardTypeId;
    @Column(name = "LIMIT_DAY_ORDER")
    private Integer limitDayOrder;
    @Column(name = "LIMIT_DAY_AMOUNT")
    private Integer limitDayAmount;
    @Column(name = "LIMIT_MONTH_ORDER")
    private Integer limitMonthOrder;
    @Column(name = "LIMIT_MONTH_AMOUNT")
    private Integer limitMonthAmount;
    @Column(name = "DAY_ORDER")
    private Integer dayOrderNumber;
    @Column(name = "DAY_AMOUNT")
    private Integer dayOrderAmount;
    @Column(name = "MONTH_ORDER")
    private Integer monthOrderNumber;
    @Column(name = "MONTH_AMOUNT")
    private Integer monthOrderAmount;
    @Column(name = "NOTED")
    private String noted;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;

}

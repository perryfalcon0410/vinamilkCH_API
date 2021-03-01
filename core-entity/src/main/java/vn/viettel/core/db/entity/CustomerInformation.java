package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.db.entity.status.converter.ObjectConverter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customer_information")
public class CustomerInformation extends BaseEntity {

    @Column(name = "object")
    @Convert(converter = ObjectConverter.class)
    private Object object;

    @Column(name = "object_id")
    private Long objectId;

    @Column(length = 64)
    private String name;

    private Byte gender;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(length = 128)
    private String zipcode;

    @Column(length = 128)
    private String address;

    @Column(length = 128)
    private String telephone;

    @Column(length = 10)
    private String customerNumber;
    
    @Column(name = "payjp_customer_id", length = 64, nullable = true)
    private String payjpCustomerId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonManagedReference
    private User user;

    public CustomerInformation() {
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPayjpCustomerId() {
        return payjpCustomerId;
    }

    public void setPayjpCustomerId(String payjpCustomerId) {
        this.payjpCustomerId = payjpCustomerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
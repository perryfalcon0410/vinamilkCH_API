package vn.viettel.core.db.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    @Column(name = "customer_number", length = 10)
    private String customerNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "menu_id")
    private Long menuId;

    @Lob
    @Column(name = "message_to_shop")
    private String messageToShop;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Column(name = "phone_number", nullable = false, length = 255)
    private String phoneNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_date")
    private Date reservationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;

    private Long type;

    private Boolean isCancel;

    @Column(name = "is_visit_store")
    private Boolean isVisitStore;

    @Column(name = "requested")
    private Integer requested;

    @Column(name = "reservation_type")
    private Integer reservationType;

    public Reservation() {
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getMenuId() {
        return this.menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMessageToShop() {
        return this.messageToShop;
    }

    public void setMessageToShop(String messageToShop) {
        this.messageToShop = messageToShop;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfPeople() {
        return this.numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getReservationDate() {
        return this.reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Boolean getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Boolean isCancel) {
        this.isCancel = isCancel;
    }

    public Boolean getVisitStore() {
        return isVisitStore;
    }

    public void setVisitStore(Boolean visitStore) {
        isVisitStore = visitStore;
    }

    public Boolean getCancel() {
        return isCancel;
    }

    public void setCancel(Boolean cancel) {
        isCancel = cancel;
    }

    public Integer getRequested() {
        return requested;
    }

    public void setRequested(Integer requested) {
        this.requested = requested;
    }

    public Integer getReservationType() {
        return reservationType;
    }

    public void setReservationType(Integer reservationType) {
        this.reservationType = reservationType;
    }
}
package vn.viettel.core.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "sale_order", schema = "sale_services")
public class SaleOrder {
    private Long id;
    private long saleOrderId;
    private String shopCode;
    private Long staffId;
    private Long customerId;
    private String orderNumber;
    private Timestamp orderDate;
    private Byte orderType;
    private Double amount;
    private Double discount;
    private Double total;
    private Integer cashierId;
    private String description;
    private String note;
    private Double totalWeight;
    private Integer totalDetail;
    private Timestamp timePrint;
    private Timestamp stockDate;
    private String createUser;
    private Timestamp createDate;
    private String updateUser;
    private Timestamp updateDate;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @Column(name = "SALE_ORDER_ID")
    public long getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(long saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    @Basic
    @Column(name = "SHOP_CODE")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Basic
    @Column(name = "STAFF_ID")
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    @Basic
    @Column(name = "CUSTOMER_ID")
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Basic
    @Column(name = "ORDER_NUMBER")
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Basic
    @Column(name = "ORDER_DATE")
    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    @Basic
    @Column(name = "ORDER_TYPE")
    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    @Basic
    @Column(name = "AMOUNT")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Basic
    @Column(name = "DISCOUNT")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Basic
    @Column(name = "TOTAL")
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Basic
    @Column(name = "CASHIER_ID")
    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "NOTE")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Basic
    @Column(name = "TOTAL_WEIGHT")
    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    @Basic
    @Column(name = "TOTAL_DETAIL")
    public Integer getTotalDetail() {
        return totalDetail;
    }

    public void setTotalDetail(Integer totalDetail) {
        this.totalDetail = totalDetail;
    }

    @Basic
    @Column(name = "TIME_PRINT")
    public Timestamp getTimePrint() {
        return timePrint;
    }

    public void setTimePrint(Timestamp timePrint) {
        this.timePrint = timePrint;
    }

    @Basic
    @Column(name = "STOCK_DATE")
    public Timestamp getStockDate() {
        return stockDate;
    }

    public void setStockDate(Timestamp stockDate) {
        this.stockDate = stockDate;
    }

    @Basic
    @Column(name = "CREATE_USER")
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "UPDATE_USER")
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Basic
    @Column(name = "UPDATE_DATE")
    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleOrder that = (SaleOrder) o;
        return saleOrderId == that.saleOrderId && Objects.equals(shopCode, that.shopCode) && Objects.equals(staffId, that.staffId) && Objects.equals(customerId, that.customerId) && Objects.equals(orderNumber, that.orderNumber) && Objects.equals(orderDate, that.orderDate) && Objects.equals(orderType, that.orderType) && Objects.equals(amount, that.amount) && Objects.equals(discount, that.discount) && Objects.equals(total, that.total) && Objects.equals(cashierId, that.cashierId) && Objects.equals(description, that.description) && Objects.equals(note, that.note) && Objects.equals(totalWeight, that.totalWeight) && Objects.equals(totalDetail, that.totalDetail) && Objects.equals(timePrint, that.timePrint) && Objects.equals(stockDate, that.stockDate) && Objects.equals(createUser, that.createUser) && Objects.equals(createDate, that.createDate) && Objects.equals(updateUser, that.updateUser) && Objects.equals(updateDate, that.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleOrderId, shopCode, staffId, customerId, orderNumber, orderDate, orderType, amount, discount, total, cashierId, description, note, totalWeight, totalDetail, timePrint, stockDate, createUser, createDate, updateUser, updateDate);
    }
}

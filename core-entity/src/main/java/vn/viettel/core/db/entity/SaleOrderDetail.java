package vn.viettel.core.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "sale_order_detail", schema = "sale_services")
public class SaleOrderDetail {
    private Long id;
    private long saleOrderDetailId;
    private Timestamp orderDate;
    private Long staffId;
    private Long productId;
    private Integer convfact;
    private Integer catId;
    private Integer quantity;
    private Integer quantityRetail;
    private Integer quantityPackage;
    private Byte isFreeItem;
    private Double discountPercent;
    private Double discountAmount;
    private Double amount;
    private Integer priceId;
    private Double price;
    private Double priceNotVat;
    private Double packagePrice;
    private Double packagePriceNotVat;
    private Integer vat;
    private Double totalWeight;
    private String programeTypeCode;
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
    @Column(name = "SALE_ORDER_DETAIL_ID")
    public long getSaleOrderDetailId() {
        return saleOrderDetailId;
    }

    public void setSaleOrderDetailId(long saleOrderDetailId) {
        this.saleOrderDetailId = saleOrderDetailId;
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
    @Column(name = "STAFF_ID")
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    @Basic
    @Column(name = "PRODUCT_ID")
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Basic
    @Column(name = "CONVFACT")
    public Integer getConvfact() {
        return convfact;
    }

    public void setConvfact(Integer convfact) {
        this.convfact = convfact;
    }

    @Basic
    @Column(name = "CAT_ID")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Basic
    @Column(name = "QUANTITY")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Basic
    @Column(name = "QUANTITY_RETAIL")
    public Integer getQuantityRetail() {
        return quantityRetail;
    }

    public void setQuantityRetail(Integer quantityRetail) {
        this.quantityRetail = quantityRetail;
    }

    @Basic
    @Column(name = "QUANTITY_PACKAGE")
    public Integer getQuantityPackage() {
        return quantityPackage;
    }

    public void setQuantityPackage(Integer quantityPackage) {
        this.quantityPackage = quantityPackage;
    }

    @Basic
    @Column(name = "IS_FREE_ITEM")
    public Byte getIsFreeItem() {
        return isFreeItem;
    }

    public void setIsFreeItem(Byte isFreeItem) {
        this.isFreeItem = isFreeItem;
    }

    @Basic
    @Column(name = "DISCOUNT_PERCENT")
    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Basic
    @Column(name = "DISCOUNT_AMOUNT")
    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
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
    @Column(name = "PRICE_ID")
    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    @Basic
    @Column(name = "PRICE")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "PRICE_NOT_VAT")
    public Double getPriceNotVat() {
        return priceNotVat;
    }

    public void setPriceNotVat(Double priceNotVat) {
        this.priceNotVat = priceNotVat;
    }

    @Basic
    @Column(name = "PACKAGE_PRICE")
    public Double getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(Double packagePrice) {
        this.packagePrice = packagePrice;
    }

    @Basic
    @Column(name = "PACKAGE_PRICE_NOT_VAT")
    public Double getPackagePriceNotVat() {
        return packagePriceNotVat;
    }

    public void setPackagePriceNotVat(Double packagePriceNotVat) {
        this.packagePriceNotVat = packagePriceNotVat;
    }

    @Basic
    @Column(name = "VAT")
    public Integer getVat() {
        return vat;
    }

    public void setVat(Integer vat) {
        this.vat = vat;
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
    @Column(name = "PROGRAME_TYPE_CODE")
    public String getProgrameTypeCode() {
        return programeTypeCode;
    }

    public void setProgrameTypeCode(String programeTypeCode) {
        this.programeTypeCode = programeTypeCode;
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
        SaleOrderDetail that = (SaleOrderDetail) o;
        return saleOrderDetailId == that.saleOrderDetailId && Objects.equals(orderDate, that.orderDate) && Objects.equals(staffId, that.staffId) && Objects.equals(productId, that.productId) && Objects.equals(convfact, that.convfact) && Objects.equals(catId, that.catId) && Objects.equals(quantity, that.quantity) && Objects.equals(quantityRetail, that.quantityRetail) && Objects.equals(quantityPackage, that.quantityPackage) && Objects.equals(isFreeItem, that.isFreeItem) && Objects.equals(discountPercent, that.discountPercent) && Objects.equals(discountAmount, that.discountAmount) && Objects.equals(amount, that.amount) && Objects.equals(priceId, that.priceId) && Objects.equals(price, that.price) && Objects.equals(priceNotVat, that.priceNotVat) && Objects.equals(packagePrice, that.packagePrice) && Objects.equals(packagePriceNotVat, that.packagePriceNotVat) && Objects.equals(vat, that.vat) && Objects.equals(totalWeight, that.totalWeight) && Objects.equals(programeTypeCode, that.programeTypeCode) && Objects.equals(createUser, that.createUser) && Objects.equals(createDate, that.createDate) && Objects.equals(updateUser, that.updateUser) && Objects.equals(updateDate, that.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleOrderDetailId, orderDate, staffId, productId, convfact, catId, quantity, quantityRetail, quantityPackage, isFreeItem, discountPercent, discountAmount, amount, priceId, price, priceNotVat, packagePrice, packagePriceNotVat, vat, totalWeight, programeTypeCode, createUser, createDate, updateUser, updateDate);
    }
}

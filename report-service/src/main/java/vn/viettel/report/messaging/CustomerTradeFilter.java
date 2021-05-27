package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTradeFilter {
    private Long shopId;

    private String keySearch;

    private String areaCode;

    private Integer customerType;

    private Integer customerStatus;

    private String customerPhone;

    private Date fromCreateDate;

    private Date toCreateDate;

    private Date fromPurchaseDate;

    private Date toPurchaseDate;

    private Float fromSaleAmount;

    private Float toSaleAmount;

    private Date fromSaleDate;

    private Date toSaleDate;

    public CustomerTradeFilter(Long shopId, String keySearch, String areaCode, Integer customerType, Integer customerStatus, String customerPhone) {
        this.shopId = shopId;
        this.keySearch = keySearch;
        this.areaCode = areaCode;
        this.customerType = customerType;
        this.customerStatus = customerStatus;
        this.customerPhone = customerPhone;
    }

    public CustomerTradeFilter withCreateAt(Date fromCreateDate, Date toCreateDate) {
        this.fromCreateDate = fromCreateDate;
        this.toCreateDate = toCreateDate;
        return this;
    }

    public CustomerTradeFilter withPurchaseAt(Date fromPurchaseDate, Date toPurchaseDate) {
        this.fromPurchaseDate = fromPurchaseDate;
        this.toPurchaseDate = toPurchaseDate;
        return this;
    }

    public CustomerTradeFilter withSaleAmount(Float fromSaleAmount, Float toSaleAmount) {
        this.fromSaleAmount = fromSaleAmount;
        this.toSaleAmount = toSaleAmount;
        return this;
    }

    public CustomerTradeFilter withSaleAt(Date fromSaleDate, Date toSaleDate) {
        this.fromSaleDate = fromSaleDate;
        this.toSaleDate = toSaleDate;
        return this;
    }

}

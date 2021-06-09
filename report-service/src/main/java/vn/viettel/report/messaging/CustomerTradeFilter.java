package vn.viettel.report.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromCreateDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toCreateDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromPurchaseDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toPurchaseDate;

    private Float fromSaleAmount;

    private Float toSaleAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromSaleDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toSaleDate;

    public CustomerTradeFilter(Long shopId, String keySearch, String areaCode, Integer customerType, Integer customerStatus, String customerPhone) {
        this.shopId = shopId;
        this.keySearch = keySearch;
        this.areaCode = areaCode;
        this.customerType = customerType;
        this.customerStatus = customerStatus;
        this.customerPhone = customerPhone;
    }

    public CustomerTradeFilter withCreateAt(LocalDateTime fromCreateDate, LocalDateTime toCreateDate) {
        this.fromCreateDate = fromCreateDate;
        this.toCreateDate = toCreateDate;
        return this;
    }

    public CustomerTradeFilter withPurchaseAt(LocalDateTime fromPurchaseDate, LocalDateTime toPurchaseDate) {
        this.fromPurchaseDate = fromPurchaseDate;
        this.toPurchaseDate = toPurchaseDate;
        return this;
    }

    public CustomerTradeFilter withSaleAmount(Float fromSaleAmount, Float toSaleAmount) {
        this.fromSaleAmount = fromSaleAmount;
        this.toSaleAmount = toSaleAmount;
        return this;
    }

    public CustomerTradeFilter withSaleAt(LocalDateTime fromSaleDate, LocalDateTime toSaleDate) {
        this.fromSaleDate = fromSaleDate;
        this.toSaleDate = toSaleDate;
        return this;
    }

}

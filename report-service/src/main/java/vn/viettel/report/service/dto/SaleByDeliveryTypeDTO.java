package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SaleByDeliveryTypeDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "SHOP_CODE")
    private String shopCode;
    @Column(name = "SHOP_NAME")
    private String shopName;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "CUSTOMER_ADDRESS")
    private String customerAddress;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "TOTAL")
    private Float total;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "DELIVERY_TYPE")
    private String deliveryType;
    @Column(name = "ONLINE_NUMBER")
    private String onlineNumber;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "COUNT_ORDER_NUMBER")
    private Integer countOrderNumber;
}

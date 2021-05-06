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
public class PromotionProductReportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "PRODUCT_CAT_NAME")
    private String productCatName;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "TOTAL_PRICE")
    private Float totalPrice;
    @Column(name = "BAR_CODE")
    private String barCode;
    @Column(name = "UOM")
    private String uom;
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
    @Column(name = "ONLINE_NUMBER")
    private String onlineNumber;
    @Column(name = "ORDER_TYPE")
    private String orderType;

}

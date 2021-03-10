package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sale_order_detail")
public class SaleOrderDetail extends BaseEntity{
    private long saleOrderDetailId;
    private long saleOrderId;
    private LocalDateTime orderDate;
    private long shopId;
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
    private LocalDateTime createDate;
    private String updateUser;
    private LocalDateTime updateDate;

}

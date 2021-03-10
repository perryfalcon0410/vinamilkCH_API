package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sale_order")
public class SaleOrder extends BaseEntity {
//    private Long id; Base entity co id roi
    private long saleOrderId;
    private long shopId;
    private String shopCode;
    private Long staffId;
    private Long customerId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private Byte orderType;
    private Double amount;
    private Double discount;
    private Double total;
    private Integer cashierId;
    private String description;
    private String note;
    private Double totalWeight;
    private Integer totalDetail;
    private LocalDateTime timePrint;
    private LocalDateTime stockDate;
    private String createUser;
    private LocalDateTime createDate;
    private String updateUser;
    private LocalDateTime updateDate;
}

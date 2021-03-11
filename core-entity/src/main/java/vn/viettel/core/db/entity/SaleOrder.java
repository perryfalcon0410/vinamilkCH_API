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
    // ong can define column name o day
//    @Column(name = "SALE_ORDER_ID")
//    private long saleOrderId;
    @Column(name = "SHOP_ID")
    private long shopId;
    @Column(name = "SHOP_CODE")
    private String shopCode;
    @Column(name = "STAFF_ID")
    private Long staffId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "ORDER_TYPE")
    private Byte orderType;
    @Column(name = "AMOUNT")
    private Double amount;
    @Column(name = "DISCOUNT")
    private Double discount;
    @Column(name = "TOTAL")
    private Double total;
    @Column(name = "CASHIER_ID")
    private Integer cashierId;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "TOTAL_WEIGHT")
    private Double totalWeight;
    @Column(name = "TOTAL_DETAIL")
    private Integer totalDetail;
    @Column(name = "TIME_PRINT")
    private LocalDateTime timePrint;
    @Column(name = "STOCK_DATE")
    private LocalDateTime stockDate;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
//    @Column(name = "DELETE_USER")
//    private String deleteUser;
    // nen doi thanh created_by cho dong nhat nguyen project
}

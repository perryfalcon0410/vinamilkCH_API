package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PO_CONFIRM")
public class PoConfirm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PO_CODE")
    private String poCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "POCO_NUMBER")
    private String poCoNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "SALE_ORDER_NUMBER")
    private String saleOrderNumber;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "CANCEL_DATE")
    private LocalDateTime cancelDate;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "CANCEL_REASON")
    private Integer cancelReason;
    @Column(name = "CANCEL_USER")
    private String cancelUser;
    @Column(name = "DENY_DATE")
    private LocalDateTime denyDate;
    @Column(name = "DENY_REASON")
    private Integer denyReason;
    @Column(name = "DENY_USER")
    private String denyUser;
    @Column(name = "IMPORT_DATE")
    private LocalDateTime importDate;
    @Column(name = "IMPORT_CODE")
    private String importCode;
    @Column(name = "IMPORT_USER")
    private String importUser;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
}
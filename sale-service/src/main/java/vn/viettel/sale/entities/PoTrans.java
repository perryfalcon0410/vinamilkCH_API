package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PO_TRANS")
public class PoTrans extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private LocalDateTime transDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @Column(name = "POCO_NUMBER")
    private String pocoNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "DISCOUNT_AMOUNT")
    private Double discountAmount;
    @Column(name = "DISCOUNT_DESCR")
    private String discountDescr;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "IS_DEBIT")
    private Boolean isDebit;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "PO_ID")
    private Long poId;
    @Column(name = "FROM_TRANS_ID")
    private Long fromTransId;
    @Column(name = "NUM_SKU")
    private Integer numSku;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;
    @Formula("(SELECT w.WAREHOUSE_TYPE_NAME FROM WAREHOUSE_TYPE w  WHERE w.ID = WAREHOUSE_TYPE_ID )")
    private String  wareHouseTypeName;

}


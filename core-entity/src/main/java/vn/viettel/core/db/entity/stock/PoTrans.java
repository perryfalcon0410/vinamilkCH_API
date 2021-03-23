package vn.viettel.core.db.entity.stock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PO_TRANS")
public class PoTrans extends BaseEntity {
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private Date transDat;
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
    private Date orderDate;
    @Column(name = "DISCOUNT_AMOUNT")
    private Float discountAmount;
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
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}


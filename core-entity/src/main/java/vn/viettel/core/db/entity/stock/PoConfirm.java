package vn.viettel.core.db.entity.stock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PO_CONFIRM")
public class PoConfirm extends BaseEntity {
    @Column(name = "PO_CODE")
    private String poCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "SALE_ORDER_NUMBER")
    private String saleOrderNumber;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "CANCEL_DATE")
    private Date cancelDate;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "CANCEL_REASON")
    private Integer cancelReason;
    @Column(name = "CANCEL_USER")
    private String cancelUser;
    @Column(name = "DENY_DATE")
    private Date denyDate;
    @Column(name = "DENY_REASON")
    private Integer denyReason;
    @Column(name = "DENY_USER")
    private String denyUser;
    @Column(name = "IMPORT_DATE")
    private Date importDate;
    @Column(name = "IMPORT_CODE")
    private String importCode;
    @Column(name = "IMPORT_USER")
    private String importUser;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
}
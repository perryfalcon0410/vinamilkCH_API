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
@Table(name = "COMBO_PRODUCT_TRANS")
public class ComboProductTrans extends BaseEntity {
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @Column(name = "TRANS_TYPE")
    private Integer transType;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "TOTAL_QUANTITY")
    private Float totalQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @Column(name = "CREATE_USER")
    private String createUser;
}
package vn.viettel.core.db.entity.common;

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
@Table(name = "PRODUCTS")
public class Product extends BaseEntity {
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PARENT_PRODUCT_CODE")
    private String parentProductCode;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "UOM2")
    private String uom2;
    @Column(name = "CONVFACT")
    private Integer convFact;
    @Column(name = "CAT_ID")
    private Long catId;
    @Column(name = "SUB_CAT_ID")
    private Long subCatId;
    @Column(name = "BRAND_ID")
    private Long brandId;
    @Column(name = "FLAVOUR_ID")
    private Long flavourId;
    @Column(name = "PACKING_ID")
    private Long packingId;
    @Column(name = "PRODUCT_TYPE")
    private String productType;
    @Column(name = "EXPIRY_TYPE")
    private Integer expiryType;
    @Column(name = "EXPIRY_NUM")
    private Integer expiryNum;
    @Column(name = "BARCODE")
    private String barCode;
    @Column(name = "PRODUCT_LEVEL_ID")
    private Long productLevelId;
    @Column(name = "CHECK_LOT")
    private Integer checkLot;
    @Column(name = "SAFETY_STOCK")
    private Integer safetyStock;
    @Column(name = "COMMISSION")
    private Float commission;
    @Column(name = "VOLUMN")
    private Float volumn;
    @Column(name = "NET_WEIGHT")
    private Float netWeight;
    @Column(name = "GROSS_WEIGHT")
    private Float grossWeight;
    @Column(name = "SUB_CAT_T_ID")
    private Long subCatTId;
    @Column(name = "GROUP_CAT_ID")
    private Long groupCatId;
    @Column(name = "GROUP_VAT")
    private String groupVat;
    @Column(name = "REF_PRODUCT_ID")
    private Long redProductId;
    @Column(name = "REF_APPLY_DATE")
    private Date refApplyDate;
    @Column(name = "CONVFACT2")
    private Integer convFact2;
    @Column(name = "IS_CORE")
    private Boolean isCore;
    @Column(name = "IS_PURGE_PRODUCT")
    private Boolean isPurgeProduct;
    @Column(name = "IS_COMBO")
    private Boolean isComno;
    @Column(name = "COMBO_PRODUCT_ID")
    private Long comboProductId;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}

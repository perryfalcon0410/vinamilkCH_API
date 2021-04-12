package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO extends BaseDTO {

    private String productName;

    private String productCode;

    private String parentProductCode;

    private Integer status;

    private String uom1;

    private String uom2;

    private Integer convFact;

    private Long catId;

    private Long subCatId;

    private Long brandId;

    private Long flavourId;

    private Long packingId;

    private String productType;

    private Integer expiryType;

    private Integer expiryNum;

    private String barCode;

    private Long productLevelId;

    private Integer checkLot;

    private Integer safetyStock;

    private Float commission;

    private Float volumn;

    private Float netWeight;

    private Float grossWeight;

    private Long subCatTId;

    private Long groupCatId;

    private String groupVat;

    private Long redProductId;

    private Date refApplyDate;

    private Integer convFact2;

    private Boolean isCore;

    private Boolean isPurgeProduct;

    private Boolean isCombo;

    private Long comboProductId;

    private Float price;
}


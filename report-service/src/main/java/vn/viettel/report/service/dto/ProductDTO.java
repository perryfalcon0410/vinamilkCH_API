package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime refApplyDate;
    private Integer convFact2;
    private Boolean isCore;
    private Boolean isPurgeProduct;
    private Boolean isComno;
    private Long comboProductId;
}

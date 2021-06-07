package vn.viettel.promotion.service.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RPT_ZV23DTO extends BaseDTO {
    @ApiModelProperty(notes = "id khuyến mãi zv23")
    private Long id;
    @ApiModelProperty(notes = "Id chương trình khuyến mãi")
    private Long promotionProgramId;
    @ApiModelProperty(notes = "mã chương trình khuyến mãi")
    private String promotionProgramCode;
    @ApiModelProperty(notes = "thời gian bắt đầu")
    private Timestamp fromDate;
    @ApiModelProperty(notes = "thời gian kết thúc")
    private Timestamp toDate;
    @ApiModelProperty(notes = "id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "số lượng")
    private Long totalQuantity;
    @ApiModelProperty(notes = "số tiền khuyến mãi")
    private Long totalAmount;
}

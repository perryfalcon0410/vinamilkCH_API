package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin xuất nhập combo")
public class ComboProductTranDTO extends BaseDTO {

    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;

    @ApiModelProperty(notes = "Mã giao dịch")
    private String transCode;

    @ApiModelProperty(notes = "Ngày giao dịch")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;

    @ApiModelProperty(notes = "Loại giao dịch")
    private Integer transType;

    @ApiModelProperty(notes = "Ghi chú")
    private String note;

    @ApiModelProperty(notes = "Id loại kho")
    private Long wareHouseTypeId;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private Double totalAmount;

    @ApiModelProperty(notes = "Danh sách sản phẩm combo")
    List<ComboProductTransComboDTO> combos;

    @ApiModelProperty(notes = "Danh sách sản phẩm thuộc combo")
    List<ComboProductTransProductDTO> products;

    @ApiModelProperty(notes = "Tổng số lượng thuộc sản phẩm quy đổi")
    Integer productTotals = 0;

    public void addProductTotals(Integer productTotals ) {
        this.productTotals+=productTotals;
    }

}

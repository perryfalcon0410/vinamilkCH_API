package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDetailDTO extends BaseDTO {
    @ApiModelProperty(notes = "Id tồn kho")
    private Long stockCountingId;
    @ApiModelProperty(notes = "Ngành hàng")
    private String productCategory;
    @ApiModelProperty(notes = "Id kho hàng")
    private Long warehouseTypeId;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Giá sản phẩm")
    @MaxTextLength(length = 12, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Float price;
    @ApiModelProperty(notes = "Số lượng tồn kho thực tế")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer stockQuantity;
    @ApiModelProperty(notes = "Số lượng kiểm kê")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer inventoryQuantity;
    @ApiModelProperty(notes = "Số lượng chênh lệch")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer changeQuantity;
    @ApiModelProperty(notes = "Tổng tiền")
    @MaxTextLength(length = 12, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Float totalAmount;
    @ApiModelProperty(notes = "Giá trị quy đổi")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer convfact;
    @ApiModelProperty(notes = "Đơn vị packet")
    private String packetUnit;
    @ApiModelProperty(notes = "Đơn vị lẻ")
    private String unit;
    @ApiModelProperty(notes = "Số lượng Packet kiểm kê")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer packetQuantity;
    @ApiModelProperty(notes = "Số lượng lẻ kiểm kê")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer unitQuantity;
    @ApiModelProperty(notes = "Nhóm sản phẩm")
    private String productGroup;
}

package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin in hóa đơn bán hàng chi tiết sản phẩm")
public class PrintProductSaleOrderDTO {

    @ApiModelProperty(notes = "0 = Ân tên nhóm, 1 = Hiển thị tên nhóm")
    private Integer displayType;

    @ApiModelProperty(notes = "Tên nhóm")
    private String groupName;

    @ApiModelProperty(notes = "Danh sách sản phẩm mua + giảm giá")
    private List<PrintOrderItemDTO> listOrderItems;

    @ApiModelProperty(notes = "Danh sách sản phẩm khuyến mãi của nhóm")
    private List<PrintFreeItemDTO> listFreeItems;
}



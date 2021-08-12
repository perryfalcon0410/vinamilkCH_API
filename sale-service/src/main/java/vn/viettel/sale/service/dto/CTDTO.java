package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CTDTO {
    @ApiModelProperty(notes = "Mã cửa hàng")
    private String shopCode;
    @ApiModelProperty(notes = "Tên ship to")
    private String shipToName;
    @ApiModelProperty(notes = "Số Hóa đơn lẻ")
    private String invoiceNumber;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Đơn vị tính")
    private String uom1;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Kho")
    private String wareHouse;
    @ApiModelProperty(notes = "Loại đơn hàng")
    private Integer redInvoiceType;

}

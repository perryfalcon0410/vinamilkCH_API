package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HDDTExcelDTO {
    @ApiModelProperty(notes = "Mã cửa hàng")
    private String shopCode;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerCode;
    @ApiModelProperty(notes = "Người mua hàng")
    private String buyerName;
    @ApiModelProperty(notes = "Tên đơn vị")
    private String officeWorking;
    @ApiModelProperty(notes = "Địa chỉ đơn vị")
    private String officeAddress;
    @ApiModelProperty(notes = "Mã số thuế")
    private String taxCode;
    @ApiModelProperty(notes = "Số điện thoại")
    private String mobiPhone;
    @ApiModelProperty(notes = "Hình thức thanh toán")
    private Integer paymentType;
    @ApiModelProperty(notes = "Số đơn đặt hàng")
    private String orderNumbers;
    @ApiModelProperty(notes = "Số đơn đặt hàng")
    private String invoiceNumber;
    @ApiModelProperty(notes = "Mã hàng")
    private String productCode;
    @ApiModelProperty(notes = "Tên hàng")
    private String productName;
    @ApiModelProperty(notes = "Đơn vị tính")
    private String uom1;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Đơn giá")
    private Double priceNotVat;
    @ApiModelProperty(notes = "Tổng thành tiền = Số lượng * Tiền không thuế")
    private Double totalAmount;
    @ApiModelProperty(notes = "Phần trăm thuế GTGT")
    private Double GTGT;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
}

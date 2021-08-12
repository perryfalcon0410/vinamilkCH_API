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
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;
import vn.viettel.sale.entities.Product;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin hóa đơn đỏ")
public class RedInvoiceNewDataDTO extends BaseDTO {

    @ApiModelProperty(notes = "Id Cửa hàng")
    private Long shopId;

    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;

    @ApiModelProperty(notes = "Danh sách Id bán hàng")
    private List<Long> saleOrderId;

    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String redInvoiceNumber;

    @ApiModelProperty(notes = "Ngày in hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime printDate;

    @ApiModelProperty(notes = "Tên đơn vị(VAT)")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.RED_INVOICE_COMPANYNAME_MAX_LENGTH_STRING)
    private String officeWorking;

    @ApiModelProperty(notes = "Địa chỉ đơn vị(VAT)")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.RED_INVOICE_ADDRESS_MAX_LENGTH_STRING)
    private String officeAddress;

    @ApiModelProperty(notes = "Mã số thuế")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.RED_INVOICE_TAXCODE_MAX_LENGTH_STRING)
    private String taxCode;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Float totalQuantity;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private Float totalMoney;

    @ApiModelProperty(notes = "Loại thanh toán: 0-Tiền mặt, 1-Chuyển khoản")
    private Integer paymentType;

    @ApiModelProperty(notes = "Ghi chú trong hóa đơn đỏ")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.RED_INVOICE_REMARK_MAX_LENGTH_STRING)
    private String noteRedInvoice;

    @ApiModelProperty(notes = "Người mua")
    private String buyerName;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    private List<ProductDataDTO> productDataDTOS;
}
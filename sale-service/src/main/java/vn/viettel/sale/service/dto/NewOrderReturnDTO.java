package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NewOrderReturnDTO {
    @ApiModelProperty(notes = "số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "ngày mua hàng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "id nhân viên")
    private Long salemanId;
    @ApiModelProperty(notes = "id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "id loại kho")
    private Long wareHouseTypeId;
    @ApiModelProperty(notes = "tổng tiền trước chiết khấu")
    private Float amount;
    @ApiModelProperty(notes = "tổng tiến khuyến mãi")
    private Float totalPromotion;
    @ApiModelProperty(notes = "tổng tiền phải trả")
    private Float total;
    @ApiModelProperty(notes = "tổng tiền khách trả")
    private Float totalPaid;
    @ApiModelProperty(notes = "tiền thừa")
    private Float balance;
    @ApiModelProperty(notes = "ghi chú")
    private String note;
    @ApiModelProperty(notes = "loại đơn hàng")
    private Integer type;
    @ApiModelProperty(notes = "đơn hàng tham chiếu")
    private Long fromSaleOrderId;
    @ApiModelProperty(notes = "tiền tích lũy sử dụng trong đơn hàng")
    private Float memberCardAmount;
    @ApiModelProperty(notes = "tiền sử dụng voucher")
    private Float totalVoucher;
    @ApiModelProperty(notes = "loại thanh toán")
    private Integer paymentType;
    @ApiModelProperty(notes = "loại giao hàng")
    private Integer deliveryType;
    @ApiModelProperty(notes = "tổng tiền tích lũy của KH trước khi mua đơn hàng hiện tại")
    private Float totalCustomerPurchase;
    @ApiModelProperty(notes = "loại đơn online hay offline")
    private Integer orderType;
    @ApiModelProperty(notes = "số đơn online")
    private String onlineNumber;
    @ApiModelProperty(notes = "khuyến mãi tự động trước thuế")
    private Float autoPromotionNotVat;
    @ApiModelProperty(notes = "khuyễn mãi tự động sau thuế")
    private Float autoPromotionVat;
    @ApiModelProperty(notes = "khuyến mãi tự động")
    private Float autoPromotion;
    @ApiModelProperty(notes = "khuyến mãi tay")
    private Float zmPromotion;
    @ApiModelProperty(notes = "tổng khuyến mãi trước thuế")
    private Float totalPromotionNotVat;
    @ApiModelProperty(notes = "tiền tích lũy khách hàng")
    private Float customerPurchase;
    @ApiModelProperty(notes = "số phiếu F1")
    private String f1Number;
    @ApiModelProperty(notes = "số tiền sử dụng mã giảm giá")
    private Float discountCodeAmount;
    @ApiModelProperty(notes = "đối với đơn online")
    private Integer onlineSubType;
    @ApiModelProperty(notes = "đã in hóa đơn đỏ hay chưa")
    private Boolean usedRedInvoice;
    @ApiModelProperty(notes = "tên cơ quan")
    private String redInvoiceCompanyName;
    @ApiModelProperty(notes = "mã số fax")
    private String redInvoiceTaxCode;
    @ApiModelProperty(notes = "địa chỉ cơ quan")
    private String redInvoiceAddress;
    @ApiModelProperty(notes = "ghi chú trên hóa đơn đỏ")
    private String redInvoiceRemark;
    @ApiModelProperty(notes = "id lý do")
    private String reasonId;
    @ApiModelProperty(notes = "mô tả lý do")
    private String reasonDesc;
    @ApiModelProperty(notes = "ngày tạo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    @ApiModelProperty(notes = "ngày cập nhật")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;
}

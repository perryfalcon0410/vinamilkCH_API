package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Danh sách các khuyến mãi cho đơn hàng")
public class SalePromotionDTO {
    
    @ApiModelProperty(notes = "Loại khuyến mãi: 0 - KM tự động, 1 - KM tay")
    private Integer promotionType = 0;

    @ApiModelProperty(notes = "Xác định có dược hưởng khuyến mãi này hay không")
    private Boolean isUse;

    @ApiModelProperty(notes = "Id chương trình khuyến mãi")
    private Long programId;

    @ApiModelProperty(notes = "Tên chương trình")
    private String promotionProgramName;

    @ApiModelProperty(notes = "Mã chương trình")
    private String promotionProgramCode;

    @ApiModelProperty(notes = "Loại chương trình: zv01 zv02...")
    private String programType;

    @ApiModelProperty(notes = "Khuyến mãi (KM) có được chỉnh sửa số lượng. KM tay có được thêm sản phẩm (tất cả KM tay đều được nhập số lượng)")
    private Boolean isEditable;

    @ApiModelProperty(notes = "Cho trường hợp one free item dc sửa số lượng nhỏ hơn = số lượng cơ cấu")
    private Integer editable;

    @ApiModelProperty(notes = "Loại rằng buộc số lượng khuyến mãi 1: one; =0: All")
    private Integer contraintType;

    @ApiModelProperty(notes = "Danh sách sản phẩm khuyến mãi")
    List<FreeProductDTO> products;

    @ApiModelProperty(notes = "Giới hạn số suất")
    private Double numberLimited;

    @ApiModelProperty(notes = "Số xuất phân bổ cho shop")
    private Double maxShopQty;

    @ApiModelProperty(notes = "Số xuất shop đã sử dụng")
    private Double receiveShopQty;

    @ApiModelProperty(notes = "Mhuyến mãi tiền hoặc phần trăm")
    SalePromotionDiscountDTO amount;

    @ApiModelProperty(notes = "Tổng số lượng khuyến mãi")
    private Integer totalQty;

    @ApiModelProperty(notes = "Tổng số tiền khuyến mãi gồm thuê")
    private Double totalAmtInTax = 0.0;

    @ApiModelProperty(notes = "Tổng số tiền khuyến mãi chưa gồm thuế")
    private Double totalAmtExTax = 0.0;

    @ApiModelProperty(notes = "Đơn hàng có được trả hàng không")
    private Boolean isReturn;

    @ApiModelProperty(notes = "Tiền tích lũy cho zv23")
    private Double zv23Amount;

    @ApiModelProperty(notes = "Danh sách sản phẩm được khuyến mãi")
    private List<Long> lstProductHasPromtion;

    @ApiModelProperty(notes = "khuyến mãi tay cần update với given type = 3")
    private List<PromotionProgramDiscountDTO> discountDTOs;

    @ApiModelProperty(notes = "Khuyến mãi bị ảnh hưởng bới sự thay đổi tiền của khuyến mãi tay nhập tiền")
    private boolean affected = false;

    @ApiModelProperty(notes = "Khuyến mãi cần gửi xuống khi gọi api tính toán")
    private boolean reCalculated = false;

    public Double getTotalAmtInTax() {
        if(totalAmtInTax == null) totalAmtInTax = 0.0;
        return (double)Math.round(totalAmtInTax);
    }

    public Double getTotalAmtExTax() {
        if(totalAmtExTax == null) totalAmtExTax = 0.0;
        return (double)Math.round(totalAmtExTax);
    }
}

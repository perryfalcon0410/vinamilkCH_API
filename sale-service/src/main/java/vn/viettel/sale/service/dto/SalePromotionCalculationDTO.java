package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Kết quả tính toán khuyến mãi")
public class SalePromotionCalculationDTO {
    
    @ApiModelProperty(notes = "Tiền khuyến mãi")
    private Double promotionAmount = 0.0;

    @ApiModelProperty(notes = "Tiền cần thanh toán")
    private Double paymentAmount = 0.0;

    @ApiModelProperty(notes = "Danh sách khuyến mãi")
    List<SalePromotionDTO> lstSalePromotions;

    @ApiModelProperty(notes = "Trạng thái voucher")
    private Boolean lockVoucher;
}

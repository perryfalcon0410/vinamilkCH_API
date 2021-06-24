package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeDTO {

    @ApiModelProperty(notes = "Mã giảm giá")
    private String discountCode;

    @ApiModelProperty(notes = "Số tiền giảm giá")
    private Double discountAmount;

    public DiscountCodeDTO(String discountCode) {
        this.discountCode = discountCode;
    }

}

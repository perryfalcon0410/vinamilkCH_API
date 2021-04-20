package vn.viettel.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherSaleProductDTO extends BaseDTO {
    private Long voucherProgramId;
    private Long productId;
    private Integer status;
}

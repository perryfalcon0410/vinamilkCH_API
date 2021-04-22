package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductDetailDTO extends BaseDTO {

    private Long comboProductId;

    private Long productId;

    private Float factor;

    private Float productPrice;

    private Integer status;
}

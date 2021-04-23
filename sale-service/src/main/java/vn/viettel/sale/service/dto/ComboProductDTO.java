package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductDTO extends BaseDTO {

    private String productCode;

    private String productName;

    private Long refProductId;

    private Integer numProduct;

    private Float productPrice;

    private Integer status;

    List<ComboProductDetailDTO> details;
}

package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportProductTransDTO extends BaseDTO {

    private ShopDTO shop;

    private ReportProductTransDetailDTO info;

    private List<ReportProductCatDTO> saleProducts;

    private List<ReportProductCatDTO> promotionProducts;

}

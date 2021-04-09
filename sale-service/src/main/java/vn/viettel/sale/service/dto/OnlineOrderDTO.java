package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnlineOrderDTO extends BaseDTO {

    private String orderNumber;

    private String orderInfo;

    List<ProductDTO> products;

    CustomerDTO customer;

}
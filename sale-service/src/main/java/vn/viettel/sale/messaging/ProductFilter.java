package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private Long shopId;
    private String keyWord;
    private Long customerId;
    private Long productInfoId;
    private Integer status;
}

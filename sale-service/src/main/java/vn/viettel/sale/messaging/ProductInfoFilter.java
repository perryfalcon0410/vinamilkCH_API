package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoFilter {
    private Integer status;
    private Integer type;
    private Long productInfoId;
    private Long customerTypeId;
}
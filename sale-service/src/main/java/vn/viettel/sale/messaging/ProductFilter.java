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
    private String keyWord;
    private Long customerTypeId;
    private Integer status;
    private Long shopId;
    private Long customerId;
}

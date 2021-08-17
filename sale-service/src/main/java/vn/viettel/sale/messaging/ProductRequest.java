package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @ApiModelProperty(notes = "Từ khóa")
    private String keyWord;
    @ApiModelProperty(notes = "Danh sách id sản phẩm")
    private List<Long> productIds;
}

package vn.viettel.promotion.service.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "Sản phẩm loại trừ")
public class RejectedProductDTO {
    @ApiModelProperty(notes = "id sản phẩm để kiểm tra")
    private Long productId;
    @ApiModelProperty(notes = "Danh sách id của những sản phẩm bị loại trừ")
    private List<Long> ids;
}

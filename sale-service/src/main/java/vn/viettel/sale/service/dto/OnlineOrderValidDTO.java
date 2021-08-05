package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OnlineOrderValidDTO {
    @ApiModelProperty(notes = "Có được chỉnh sửa đơn Online")
    private boolean isEditable;
    @ApiModelProperty(notes = "Có được tạo tay đơn Online")
    private boolean isManuallyCreatable;
}
